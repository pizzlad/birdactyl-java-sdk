package io.birdactyl.sdk;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.birdactyl.sdk.proto.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class PanelAPIAsync {
    private static final Gson gson = new Gson();
    private final PanelServiceGrpc.PanelServiceFutureStub stub;
    private final Executor executor;

    public PanelAPIAsync(PanelServiceGrpc.PanelServiceFutureStub stub) {
        this(stub, MoreExecutors.directExecutor());
    }

    public PanelAPIAsync(PanelServiceGrpc.PanelServiceFutureStub stub, Executor executor) {
        this.stub = stub;
        this.executor = executor;
    }

    private <T, R> CompletableFuture<R> toCompletable(ListenableFuture<T> future, java.util.function.Function<T, R> mapper) {
        CompletableFuture<R> cf = new CompletableFuture<>();
        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                try {
                    cf.complete(mapper.apply(result));
                } catch (Exception e) {
                    cf.completeExceptionally(e);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                cf.completeExceptionally(t);
            }
        }, executor);
        return cf;
    }

    private <T> CompletableFuture<Void> toCompletableVoid(ListenableFuture<T> future) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                cf.complete(null);
            }
            @Override
            public void onFailure(Throwable t) {
                cf.completeExceptionally(t);
            }
        }, executor);
        return cf;
    }

    public CompletableFuture<Void> log(String level, String message) {
        return toCompletableVoid(stub.log(LogRequest.newBuilder().setLevel(level).setMessage(message).build()));
    }

    public CompletableFuture<PanelAPI.Server> getServer(String id) {
        return toCompletable(stub.getServer(IDRequest.newBuilder().setId(id).build()), PanelAPI.Server::new);
    }

    public CompletableFuture<List<PanelAPI.Server>> listServers() {
        return toCompletable(stub.listServers(ListServersRequest.getDefaultInstance()),
                resp -> resp.getServersList().stream().map(PanelAPI.Server::new).collect(Collectors.toList()));
    }

    public CompletableFuture<List<PanelAPI.Server>> listServersByUser(String userId) {
        return toCompletable(stub.listServers(ListServersRequest.newBuilder().setUserId(userId).build()),
                resp -> resp.getServersList().stream().map(PanelAPI.Server::new).collect(Collectors.toList()));
    }

    public CompletableFuture<PanelAPI.Server> createServer(String name, String userId, String nodeId, String packageId, int memory, int cpu, int disk) {
        return toCompletable(stub.createServer(CreateServerRequest.newBuilder()
                .setName(name).setUserId(userId).setNodeId(nodeId).setPackageId(packageId)
                .setMemory(memory).setCpu(cpu).setDisk(disk).build()), PanelAPI.Server::new);
    }

    public CompletableFuture<Void> deleteServer(String id) {
        return toCompletableVoid(stub.deleteServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<PanelAPI.Server> updateServer(String id, String name, Integer memory, Integer cpu, Integer disk) {
        UpdateServerRequest.Builder req = UpdateServerRequest.newBuilder().setId(id);
        if (name != null) req.setName(name);
        if (memory != null) req.setMemory(memory);
        if (cpu != null) req.setCpu(cpu);
        if (disk != null) req.setDisk(disk);
        return toCompletable(stub.updateServer(req.build()), PanelAPI.Server::new);
    }

    public CompletableFuture<Void> startServer(String id) {
        return toCompletableVoid(stub.startServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> stopServer(String id) {
        return toCompletableVoid(stub.stopServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> restartServer(String id) {
        return toCompletableVoid(stub.restartServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> killServer(String id) {
        return toCompletableVoid(stub.killServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> suspendServer(String id) {
        return toCompletableVoid(stub.suspendServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> unsuspendServer(String id) {
        return toCompletableVoid(stub.unsuspendServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> reinstallServer(String id) {
        return toCompletableVoid(stub.reinstallServer(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> transferServer(String serverId, String targetNodeId) {
        return toCompletableVoid(stub.transferServer(TransferServerRequest.newBuilder().setServerId(serverId).setTargetNodeId(targetNodeId).build()));
    }

    public CompletableFuture<List<String>> getConsoleLog(String serverId, int lines) {
        return toCompletable(stub.getConsoleLog(ConsoleLogRequest.newBuilder().setServerId(serverId).setLines(lines).build()),
                ConsoleLogResponse::getLinesList);
    }

    public CompletableFuture<Void> sendCommand(String serverId, String command) {
        return toCompletableVoid(stub.sendCommand(SendCommandRequest.newBuilder().setServerId(serverId).setCommand(command).build()));
    }

    public CompletableFuture<PanelAPI.ServerStats> getServerStats(String serverId) {
        return toCompletable(stub.getServerStats(IDRequest.newBuilder().setId(serverId).build()),
                s -> new PanelAPI.ServerStats(s.getMemoryBytes(), s.getMemoryLimit(), s.getCpuPercent(), s.getDiskBytes(), s.getNetworkRx(), s.getNetworkTx(), s.getState()));
    }

    public CompletableFuture<Void> addAllocation(String serverId, int port) {
        return toCompletableVoid(stub.addAllocation(AllocationRequest.newBuilder().setServerId(serverId).setPort(port).build()));
    }

    public CompletableFuture<Void> deleteAllocation(String serverId, int port) {
        return toCompletableVoid(stub.deleteAllocation(AllocationRequest.newBuilder().setServerId(serverId).setPort(port).build()));
    }

    public CompletableFuture<Void> setPrimaryAllocation(String serverId, int port) {
        return toCompletableVoid(stub.setPrimaryAllocation(AllocationRequest.newBuilder().setServerId(serverId).setPort(port).build()));
    }

    public CompletableFuture<Void> updateServerVariables(String serverId, Map<String, String> variables) {
        return toCompletableVoid(stub.updateServerVariables(UpdateVariablesRequest.newBuilder().setServerId(serverId).putAllVariables(variables).build()));
    }

    public CompletableFuture<Void> compressFiles(String serverId, List<String> paths, String destination) {
        return toCompletableVoid(stub.compressFiles(CompressRequest.newBuilder().setServerId(serverId).addAllPaths(paths).setDestination(destination).build()));
    }

    public CompletableFuture<Void> decompressFile(String serverId, String path) {
        return toCompletableVoid(stub.decompressFile(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build()));
    }

    public CompletableFuture<PanelAPI.User> getUser(String id) {
        return toCompletable(stub.getUser(IDRequest.newBuilder().setId(id).build()), PanelAPI.User::new);
    }

    public CompletableFuture<PanelAPI.User> getUserByEmail(String email) {
        return toCompletable(stub.getUserByEmail(EmailRequest.newBuilder().setEmail(email).build()), PanelAPI.User::new);
    }

    public CompletableFuture<PanelAPI.User> getUserByUsername(String username) {
        return toCompletable(stub.getUserByUsername(UsernameRequest.newBuilder().setUsername(username).build()), PanelAPI.User::new);
    }

    public CompletableFuture<List<PanelAPI.User>> listUsers() {
        return toCompletable(stub.listUsers(ListUsersRequest.getDefaultInstance()),
                resp -> resp.getUsersList().stream().map(PanelAPI.User::new).collect(Collectors.toList()));
    }

    public CompletableFuture<PanelAPI.User> createUser(String email, String username, String password) {
        return toCompletable(stub.createUser(CreateUserRequest.newBuilder()
                .setEmail(email).setUsername(username).setPassword(password).build()), PanelAPI.User::new);
    }

    public CompletableFuture<PanelAPI.User> updateUser(String id, String username, String email) {
        UpdateUserRequest.Builder req = UpdateUserRequest.newBuilder().setId(id);
        if (username != null) req.setUsername(username);
        if (email != null) req.setEmail(email);
        return toCompletable(stub.updateUser(req.build()), PanelAPI.User::new);
    }

    public CompletableFuture<Void> deleteUser(String id) {
        return toCompletableVoid(stub.deleteUser(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> banUser(String id) {
        return toCompletableVoid(stub.banUser(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> unbanUser(String id) {
        return toCompletableVoid(stub.unbanUser(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> setAdmin(String id) {
        return toCompletableVoid(stub.setAdmin(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> revokeAdmin(String id) {
        return toCompletableVoid(stub.revokeAdmin(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> forcePasswordReset(String id) {
        return toCompletableVoid(stub.forcePasswordReset(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<Void> setUserResources(String userId, Integer ramLimit, Integer cpuLimit, Integer diskLimit, Integer serverLimit) {
        SetUserResourcesRequest.Builder req = SetUserResourcesRequest.newBuilder().setUserId(userId);
        if (ramLimit != null) req.setRamLimit(ramLimit);
        if (cpuLimit != null) req.setCpuLimit(cpuLimit);
        if (diskLimit != null) req.setDiskLimit(diskLimit);
        if (serverLimit != null) req.setServerLimit(serverLimit);
        return toCompletableVoid(stub.setUserResources(req.build()));
    }

    public CompletableFuture<List<PanelAPI.Node>> listNodes() {
        return toCompletable(stub.listNodes(Empty.getDefaultInstance()),
                resp -> resp.getNodesList().stream().map(PanelAPI.Node::new).collect(Collectors.toList()));
    }

    public CompletableFuture<PanelAPI.Node> getNode(String id) {
        return toCompletable(stub.getNode(IDRequest.newBuilder().setId(id).build()), PanelAPI.Node::new);
    }

    public CompletableFuture<PanelAPI.NodeWithToken> createNode(String name, String fqdn, int port) {
        return toCompletable(stub.createNode(CreateNodeRequest.newBuilder().setName(name).setFqdn(fqdn).setPort(port).build()),
                resp -> new PanelAPI.NodeWithToken(new PanelAPI.Node(resp.getNode()), resp.getToken()));
    }

    public CompletableFuture<Void> deleteNode(String id) {
        return toCompletableVoid(stub.deleteNode(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<String> resetNodeToken(String id) {
        return toCompletable(stub.resetNodeToken(IDRequest.newBuilder().setId(id).build()), NodeToken::getToken);
    }

    public CompletableFuture<List<PanelAPI.File>> listFiles(String serverId, String path) {
        return toCompletable(stub.listFiles(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build()),
                resp -> resp.getFilesList().stream().map(PanelAPI.File::new).collect(Collectors.toList()));
    }

    public CompletableFuture<byte[]> readFile(String serverId, String path) {
        return toCompletable(stub.readFile(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build()),
                resp -> resp.getContent().toByteArray());
    }

    public CompletableFuture<Void> writeFile(String serverId, String path, byte[] content) {
        return toCompletableVoid(stub.writeFile(WriteFileRequest.newBuilder().setServerId(serverId).setPath(path).setContent(ByteString.copyFrom(content)).build()));
    }

    public CompletableFuture<Void> deleteFile(String serverId, String path) {
        return toCompletableVoid(stub.deleteFile(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build()));
    }

    public CompletableFuture<Void> createFolder(String serverId, String path) {
        return toCompletableVoid(stub.createFolder(FilePathRequest.newBuilder().setServerId(serverId).setPath(path).build()));
    }

    public CompletableFuture<Void> moveFile(String serverId, String from, String to) {
        return toCompletableVoid(stub.moveFile(MoveFileRequest.newBuilder().setServerId(serverId).setFrom(from).setTo(to).build()));
    }

    public CompletableFuture<Void> copyFile(String serverId, String from, String to) {
        return toCompletableVoid(stub.copyFile(MoveFileRequest.newBuilder().setServerId(serverId).setFrom(from).setTo(to).build()));
    }

    public CompletableFuture<List<PanelAPI.Database>> listDatabases(String serverId) {
        return toCompletable(stub.listDatabases(IDRequest.newBuilder().setId(serverId).build()),
                resp -> resp.getDatabasesList().stream().map(PanelAPI.Database::new).collect(Collectors.toList()));
    }

    public CompletableFuture<PanelAPI.Database> createDatabase(String serverId, String name) {
        return toCompletable(stub.createDatabase(CreateDatabaseRequest.newBuilder().setServerId(serverId).setName(name).build()), PanelAPI.Database::new);
    }

    public CompletableFuture<Void> deleteDatabase(String id) {
        return toCompletableVoid(stub.deleteDatabase(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<PanelAPI.Database> rotateDatabasePassword(String id) {
        return toCompletable(stub.rotateDatabasePassword(IDRequest.newBuilder().setId(id).build()), PanelAPI.Database::new);
    }

    public CompletableFuture<List<PanelAPI.DatabaseHost>> listDatabaseHosts() {
        return toCompletable(stub.listDatabaseHosts(Empty.getDefaultInstance()),
                resp -> resp.getHostsList().stream().map(PanelAPI.DatabaseHost::new).collect(Collectors.toList()));
    }

    public CompletableFuture<List<PanelAPI.Backup>> listBackups(String serverId) {
        return toCompletable(stub.listBackups(IDRequest.newBuilder().setId(serverId).build()),
                resp -> resp.getBackupsList().stream().map(PanelAPI.Backup::new).collect(Collectors.toList()));
    }

    public CompletableFuture<Void> createBackup(String serverId, String name) {
        return toCompletableVoid(stub.createBackup(CreateBackupRequest.newBuilder().setServerId(serverId).setName(name).build()));
    }

    public CompletableFuture<Void> deleteBackup(String serverId, String backupId) {
        return toCompletableVoid(stub.deleteBackup(DeleteBackupRequest.newBuilder().setServerId(serverId).setBackupId(backupId).build()));
    }

    public CompletableFuture<List<PanelAPI.Package>> listPackages() {
        return toCompletable(stub.listPackages(Empty.getDefaultInstance()),
                resp -> resp.getPackagesList().stream().map(PanelAPI.Package::new).collect(Collectors.toList()));
    }

    public CompletableFuture<PanelAPI.Package> getPackage(String id) {
        return toCompletable(stub.getPackage(IDRequest.newBuilder().setId(id).build()), PanelAPI.Package::new);
    }

    public CompletableFuture<PanelAPI.Package> createPackage(String name, String description, String dockerImage, String startupCmd, String stopCmd, String configFiles, int memory, int cpu, int disk, boolean isPublic) {
        return toCompletable(stub.createPackage(CreatePackageRequest.newBuilder()
                .setName(name).setDescription(description).setDockerImage(dockerImage)
                .setStartupCommand(startupCmd).setStopCommand(stopCmd).setConfigFiles(configFiles)
                .setDefaultMemory(memory).setDefaultCpu(cpu).setDefaultDisk(disk).setIsPublic(isPublic).build()), PanelAPI.Package::new);
    }

    public CompletableFuture<PanelAPI.Package> updatePackage(String id, String name, String description, Integer memory, Integer cpu, Integer disk) {
        UpdatePackageRequest.Builder req = UpdatePackageRequest.newBuilder().setId(id);
        if (name != null) req.setName(name);
        if (description != null) req.setDescription(description);
        if (memory != null) req.setDefaultMemory(memory);
        if (cpu != null) req.setDefaultCpu(cpu);
        if (disk != null) req.setDefaultDisk(disk);
        return toCompletable(stub.updatePackage(req.build()), PanelAPI.Package::new);
    }

    public CompletableFuture<Void> deletePackage(String id) {
        return toCompletableVoid(stub.deletePackage(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<List<PanelAPI.IPBan>> listIPBans() {
        return toCompletable(stub.listIPBans(Empty.getDefaultInstance()),
                resp -> resp.getBansList().stream().map(PanelAPI.IPBan::new).collect(Collectors.toList()));
    }

    public CompletableFuture<PanelAPI.IPBan> createIPBan(String ip, String reason) {
        return toCompletable(stub.createIPBan(CreateIPBanRequest.newBuilder().setIp(ip).setReason(reason).build()), PanelAPI.IPBan::new);
    }

    public CompletableFuture<Void> deleteIPBan(String id) {
        return toCompletableVoid(stub.deleteIPBan(IDRequest.newBuilder().setId(id).build()));
    }

    public CompletableFuture<List<PanelAPI.Subuser>> listSubusers(String serverId) {
        return toCompletable(stub.listSubusers(IDRequest.newBuilder().setId(serverId).build()),
                resp -> resp.getSubusersList().stream().map(PanelAPI.Subuser::new).collect(Collectors.toList()));
    }

    public CompletableFuture<PanelAPI.Subuser> addSubuser(String serverId, String email, List<String> permissions) {
        return toCompletable(stub.addSubuser(AddSubuserRequest.newBuilder()
                .setServerId(serverId).setEmail(email).addAllPermissions(permissions).build()), PanelAPI.Subuser::new);
    }

    public CompletableFuture<Void> updateSubuser(String serverId, String subuserId, List<String> permissions) {
        return toCompletableVoid(stub.updateSubuser(UpdateSubuserRequest.newBuilder().setServerId(serverId).setSubuserId(subuserId).addAllPermissions(permissions).build()));
    }

    public CompletableFuture<Void> removeSubuser(String serverId, String subuserId) {
        return toCompletableVoid(stub.removeSubuser(RemoveSubuserRequest.newBuilder().setServerId(serverId).setSubuserId(subuserId).build()));
    }

    public CompletableFuture<PanelAPI.Settings> getSettings() {
        return toCompletable(stub.getSettings(Empty.getDefaultInstance()),
                s -> new PanelAPI.Settings(s.getRegistrationEnabled(), s.getServerCreationEnabled()));
    }

    public CompletableFuture<Void> setRegistrationEnabled(boolean enabled) {
        return toCompletableVoid(stub.setRegistrationEnabled(BoolRequest.newBuilder().setValue(enabled).build()));
    }

    public CompletableFuture<Void> setServerCreationEnabled(boolean enabled) {
        return toCompletableVoid(stub.setServerCreationEnabled(BoolRequest.newBuilder().setValue(enabled).build()));
    }

    public CompletableFuture<List<PanelAPI.ActivityLog>> getActivityLogs(int limit) {
        return toCompletable(stub.getActivityLogs(GetLogsRequest.newBuilder().setLimit(limit).build()),
                resp -> resp.getLogsList().stream().map(PanelAPI.ActivityLog::new).collect(Collectors.toList()));
    }

    public CompletableFuture<String> getKV(String key) {
        return toCompletable(stub.getKV(KVRequest.newBuilder().setKey(key).build()),
                resp -> resp.getFound() ? resp.getValue() : null);
    }

    public CompletableFuture<Void> setKV(String key, String value) {
        return toCompletableVoid(stub.setKV(KVSetRequest.newBuilder().setKey(key).setValue(value).build()));
    }

    public CompletableFuture<Void> deleteKV(String key) {
        return toCompletableVoid(stub.deleteKV(KVRequest.newBuilder().setKey(key).build()));
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<List<Map<String, Object>>> queryDB(String query, String... args) {
        return toCompletable(stub.queryDB(QueryDBRequest.newBuilder().setQuery(query).addAllArgs(Arrays.asList(args)).build()),
                resp -> resp.getRowsList().stream().map(row -> (Map<String, Object>) gson.fromJson(row.toStringUtf8(), Map.class)).collect(Collectors.toList()));
    }

    public CompletableFuture<Void> broadcastEvent(String eventType, Map<String, String> data) {
        return toCompletableVoid(stub.broadcastEvent(BroadcastEventRequest.newBuilder().setEventType(eventType).putAllData(data).build()));
    }

    public CompletableFuture<PanelAPI.HTTPResponse> http(String method, String url, Map<String, String> headers, byte[] body) {
        PluginHTTPRequest.Builder req = PluginHTTPRequest.newBuilder().setMethod(method).setUrl(url);
        if (headers != null) req.putAllHeaders(headers);
        if (body != null) req.setBody(ByteString.copyFrom(body));
        return toCompletable(stub.hTTPRequest(req.build()), PanelAPI.HTTPResponse::new);
    }

    public CompletableFuture<PanelAPI.HTTPResponse> httpGet(String url, Map<String, String> headers) {
        return http("GET", url, headers, null);
    }

    public CompletableFuture<PanelAPI.HTTPResponse> httpPost(String url, Map<String, String> headers, byte[] body) {
        return http("POST", url, headers, body);
    }

    public CompletableFuture<PanelAPI.HTTPResponse> httpPut(String url, Map<String, String> headers, byte[] body) {
        return http("PUT", url, headers, body);
    }

    public CompletableFuture<PanelAPI.HTTPResponse> httpDelete(String url, Map<String, String> headers) {
        return http("DELETE", url, headers, null);
    }

    public CompletableFuture<byte[]> callPlugin(String pluginId, String method, byte[] data) {
        CallPluginRequest.Builder req = CallPluginRequest.newBuilder().setPluginId(pluginId).setMethod(method);
        if (data != null) req.setData(ByteString.copyFrom(data));
        return toCompletable(stub.callPlugin(req.build()), resp -> {
            if (!resp.getError().isEmpty()) throw new RuntimeException(resp.getError());
            return resp.getData().toByteArray();
        });
    }

    public <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    public <T> CompletableFuture<T> any(List<CompletableFuture<T>> futures) {
        CompletableFuture<T> result = new CompletableFuture<>();
        for (CompletableFuture<T> f : futures) {
            f.thenAccept(result::complete);
        }
        return result;
    }
}
