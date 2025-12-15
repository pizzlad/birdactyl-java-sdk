package io.birdactyl.sdk;

import io.birdactyl.sdk.proto.*;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ConsoleStream {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final String serverId;
    private final Consumer<String> lineHandler;
    private final Consumer<Throwable> errorHandler;
    private final Runnable completionHandler;

    ConsoleStream(String serverId, Consumer<String> lineHandler, Consumer<Throwable> errorHandler, Runnable completionHandler) {
        this.serverId = serverId;
        this.lineHandler = lineHandler;
        this.errorHandler = errorHandler;
        this.completionHandler = completionHandler;
    }

    public void stop() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }

    StreamObserver<ConsoleLine> createObserver() {
        return new StreamObserver<>() {
            @Override
            public void onNext(ConsoleLine line) {
                if (running.get() && lineHandler != null) {
                    lineHandler.accept(line.getLine());
                }
            }

            @Override
            public void onError(Throwable t) {
                running.set(false);
                if (errorHandler != null) {
                    errorHandler.accept(t);
                }
            }

            @Override
            public void onCompleted() {
                running.set(false);
                if (completionHandler != null) {
                    completionHandler.run();
                }
            }
        };
    }

    public static class Builder {
        private final String serverId;
        private boolean includeHistory = true;
        private int historyLines = 100;
        private Consumer<String> lineHandler;
        private Consumer<Throwable> errorHandler;
        private Runnable completionHandler;

        public Builder(String serverId) {
            this.serverId = serverId;
        }

        public Builder includeHistory(boolean include) {
            this.includeHistory = include;
            return this;
        }

        public Builder historyLines(int lines) {
            this.historyLines = lines;
            return this;
        }

        public Builder onLine(Consumer<String> handler) {
            this.lineHandler = handler;
            return this;
        }

        public Builder onError(Consumer<Throwable> handler) {
            this.errorHandler = handler;
            return this;
        }

        public Builder onComplete(Runnable handler) {
            this.completionHandler = handler;
            return this;
        }

        StreamConsoleRequest buildRequest() {
            return StreamConsoleRequest.newBuilder()
                    .setServerId(serverId)
                    .setIncludeHistory(includeHistory)
                    .setHistoryLines(historyLines)
                    .build();
        }

        ConsoleStream build() {
            return new ConsoleStream(serverId, lineHandler, errorHandler, completionHandler);
        }
    }
}
