package io.birdactyl.sdk;

import com.google.gson.Gson;
import io.birdactyl.sdk.proto.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

public abstract class BirdactylPlugin extends PluginServiceGrpc.PluginServiceImplBase {
    private static final Gson gson = new Gson();
    private final String id;
    private String name;
    private final String version;
    private final Map<String, Function<Event, EventResult>> eventHandlers = new ConcurrentHashMap<>();
    private final Map<String, Function<Request, Response>> routeHandlers = new ConcurrentHashMap<>();
    private final Map<String, Runnable> scheduleHandlers = new ConcurrentHashMap<>();
    private final Map<String, MixinRegistration> mixinHandlers = new ConcurrentHashMap<>();
    private final List<RouteInfo> routes = new ArrayList<>();
    private final List<ScheduleInfo> schedules = new ArrayList<>();
    private final List<MixinInfo> mixins = new ArrayList<>();
    private final CountDownLatch readyLatch = new CountDownLatch(1);
    private PanelAPI api;
    private PanelAPIAsync asyncApi;
    private PanelServiceGrpc.PanelServiceStub asyncStub;
    private Executor asyncExecutor = ForkJoinPool.commonPool();
    private File dataDir;
    private boolean useDataDir = false;
    private Runnable onStartCallback;

    private static class MixinRegistration {
        final String target;
        final int priority;
        final MixinHandler handler;
        MixinRegistration(String target, int priority, MixinHandler handler) {
            this.target = target; this.priority = priority; this.handler = handler;
        }
    }

    public BirdactylPlugin(String id, String version) {
        this.id = id;
        this.name = id;
        this.version = version;
    }

    public BirdactylPlugin setName(String n) {
        this.name = n;
        return this;
    }

    public BirdactylPlugin useDataDir() {
        this.useDataDir = true;
        return this;
    }

    public BirdactylPlugin asyncExecutor(Executor executor) {
        this.asyncExecutor = executor;
        return this;
    }

    public BirdactylPlugin onStart(Runnable callback) {
        this.onStartCallback = callback;
        return this;
    }

    public void onEvent(String eventType, Function<Event, EventResult> handler) {
        eventHandlers.put(eventType, handler);
    }

    public void route(String method, String path, Function<Request, Response> handler) {
        routeHandlers.put(method + ":" + path, handler);
        routes.add(RouteInfo.newBuilder().setMethod(method).setPath(path).build());
    }

    public void schedule(String scheduleId, String cron, Runnable handler) {
        scheduleHandlers.put(scheduleId, handler);
        schedules.add(ScheduleInfo.newBuilder().setId(scheduleId).setCron(cron).build());
    }

    public void mixin(String target, MixinHandler handler) {
        mixin(target, 0, handler);
    }

    public void mixin(String target, int priority, MixinHandler handler) {
        mixinHandlers.put(target, new MixinRegistration(target, priority, handler));
        mixins.add(MixinInfo.newBuilder().setTarget(target).setPriority(priority).build());
    }

    public void registerMixin(Class<? extends MixinClass> clazz) {
        Mixin annotation = clazz.getAnnotation(Mixin.class);
        if (annotation == null) return;
        try {
            MixinClass instance = clazz.getDeclaredConstructor().newInstance();
            instance.init(this);
            mixin(annotation.value(), annotation.priority(), instance::handle);
        } catch (Exception e) {
            System.err.println("[mixin] Failed to register " + clazz.getName() + ": " + e.getMessage());
        }
    }

    public void registerMixins(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (MixinClass.class.isAssignableFrom(clazz)) {
                registerMixin((Class<? extends MixinClass>) clazz);
            }
        }
    }

    public PanelAPI api() {
        return api;
    }

    public PanelAPIAsync async() {
        return asyncApi;
    }

    public ConsoleStream streamConsole(ConsoleStream.Builder builder) {
        ConsoleStream stream = builder.build();
        asyncStub.streamConsole(builder.buildRequest(), stream.createObserver());
        return stream;
    }

    public ConsoleStream.Builder console(String serverId) {
        return new ConsoleStream.Builder(serverId);
    }

    public File dataDir() {
        return dataDir;
    }

    public File dataPath(String filename) {
        return new File(dataDir, filename);
    }

    public void start(String panelAddress, int defaultPort) throws Exception {
        int port = defaultPort;
        String dataDirPath = id;

        String[] args = System.getProperty("sun.java.command", "").split(" ");
        if (args.length > 1) {
            try { port = Integer.parseInt(args[1]); } catch (Exception ignored) {}
        }
        if (args.length > 2) {
            dataDirPath = args[2] + "/" + id + "_data";
        } else {
            dataDirPath = id + "_data";
        }

        dataDir = new File(dataDirPath);
        if (useDataDir) {
            dataDir.mkdirs();
        }

        ManagedChannel channel = ManagedChannelBuilder.forTarget(panelAddress)
                .usePlaintext()
                .intercept(new ClientInterceptor() {
                    @Override
                    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions options, Channel next) {
                        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, options)) {
                            @Override
                            public void start(Listener<RespT> listener, Metadata headers) {
                                headers.put(Metadata.Key.of("x-plugin-id", Metadata.ASCII_STRING_MARSHALLER), id);
                                super.start(listener, headers);
                            }
                        };
                    }
                })
                .build();

        api = new PanelAPI(PanelServiceGrpc.newBlockingStub(channel));
        asyncApi = new PanelAPIAsync(PanelServiceGrpc.newFutureStub(channel), asyncExecutor);
        asyncStub = PanelServiceGrpc.newStub(channel);

        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(this)
                .build()
                .start();

        System.out.println("[" + id + "] v" + version + " listening on port " + port);

        try { readyLatch.await(); } catch (InterruptedException ignored) {}

        if (onStartCallback != null) {
            onStartCallback.run();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }

    @Override
    public void getInfo(Empty request, StreamObserver<PluginInfo> response) {
        PluginInfo.Builder builder = PluginInfo.newBuilder()
                .setId(id)
                .setName(name)
                .setVersion(version)
                .addAllEvents(eventHandlers.keySet())
                .addAllRoutes(routes)
                .addAllSchedules(schedules)
                .addAllMixins(mixins);

        response.onNext(builder.build());
        response.onCompleted();
        readyLatch.countDown();
    }

    @Override
    public void onEvent(io.birdactyl.sdk.proto.Event request, StreamObserver<EventResponse> response) {
        Function<Event, EventResult> handler = eventHandlers.get(request.getType());
        EventResult result = EventResult.allow();
        if (handler != null) {
            result = handler.apply(new Event(request.getType(), request.getDataMap(), request.getSync()));
        }
        response.onNext(EventResponse.newBuilder().setAllow(result.isAllowed()).setMessage(result.getMessage()).build());
        response.onCompleted();
    }

    @Override
    public void onHTTP(HTTPRequest request, StreamObserver<HTTPResponse> response) {
        Function<Request, Response> handler = routeHandlers.get(request.getMethod() + ":" + request.getPath());
        if (handler == null) {
            for (Map.Entry<String, Function<Request, Response>> entry : routeHandlers.entrySet()) {
                String[] parts = entry.getKey().split(":", 2);
                if ((parts[0].equals("*") || parts[0].equals(request.getMethod())) && matchPath(parts[1], request.getPath())) {
                    handler = entry.getValue();
                    break;
                }
            }
        }

        Response resp;
        if (handler != null) {
            resp = handler.apply(new Request(request));
        } else {
            resp = Response.error(404, "not found");
        }

        response.onNext(HTTPResponse.newBuilder()
                .setStatus(resp.getStatus())
                .putAllHeaders(resp.getHeaders())
                .setBody(com.google.protobuf.ByteString.copyFrom(resp.getBody()))
                .build());
        response.onCompleted();
    }

    @Override
    public void onSchedule(ScheduleRequest request, StreamObserver<Empty> response) {
        Runnable handler = scheduleHandlers.get(request.getScheduleId());
        if (handler != null) {
            handler.run();
        }
        response.onNext(Empty.getDefaultInstance());
        response.onCompleted();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMixin(io.birdactyl.sdk.proto.MixinRequest request, StreamObserver<io.birdactyl.sdk.proto.MixinResponse> response) {
        MixinRegistration reg = mixinHandlers.get(request.getTarget());
        if (reg == null) {
            response.onNext(io.birdactyl.sdk.proto.MixinResponse.newBuilder()
                    .setAction(io.birdactyl.sdk.proto.MixinResponse.Action.NEXT)
                    .build());
            response.onCompleted();
            return;
        }

        Map<String, Object> input = gson.fromJson(request.getInput().toStringUtf8(), Map.class);
        Map<String, Object> chainData = null;
        if (!request.getChainData().isEmpty()) {
            chainData = gson.fromJson(request.getChainData().toStringUtf8(), Map.class);
        }

        MixinContext ctx = new MixinContext(request.getTarget(), request.getRequestId(), input, chainData);
        MixinResult result = reg.handler.handle(ctx);

        io.birdactyl.sdk.proto.MixinResponse.Builder resp = io.birdactyl.sdk.proto.MixinResponse.newBuilder();

        switch (result.getAction()) {
            case NEXT:
                resp.setAction(io.birdactyl.sdk.proto.MixinResponse.Action.NEXT);
                if (result.getModifiedInput() != null) {
                    resp.setModifiedInput(com.google.protobuf.ByteString.copyFromUtf8(gson.toJson(result.getModifiedInput())));
                }
                break;
            case RETURN:
                resp.setAction(io.birdactyl.sdk.proto.MixinResponse.Action.RETURN);
                if (result.getOutput() != null) {
                    resp.setOutput(com.google.protobuf.ByteString.copyFromUtf8(gson.toJson(result.getOutput())));
                }
                break;
            case ERROR:
                resp.setAction(io.birdactyl.sdk.proto.MixinResponse.Action.ERROR);
                resp.setError(result.getError() != null ? result.getError() : "");
                break;
        }

        for (MixinResult.Notification n : result.getNotifications()) {
            resp.addNotifications(io.birdactyl.sdk.proto.Notification.newBuilder()
                    .setTitle(n.getTitle())
                    .setMessage(n.getMessage())
                    .setType(n.getType())
                    .build());
        }

        response.onNext(resp.build());
        response.onCompleted();
    }

    @Override
    public void shutdown(Empty request, StreamObserver<Empty> response) {
        System.out.println("[" + id + "] shutdown");
        response.onNext(Empty.getDefaultInstance());
        response.onCompleted();
    }

    private boolean matchPath(String pattern, String path) {
        if (pattern.equals(path)) return true;
        if (pattern.endsWith("*")) {
            return path.startsWith(pattern.substring(0, pattern.length() - 1));
        }
        return false;
    }
}
