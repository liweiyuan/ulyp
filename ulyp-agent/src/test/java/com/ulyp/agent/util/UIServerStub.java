package com.ulyp.agent.util;

import com.ulyp.agent.MethodMatcher;
import com.ulyp.transport.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.*;

public class UIServerStub implements AutoCloseable {

    private final CompletableFuture<TCallTraceLogUploadRequest> future = new CompletableFuture<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Server server;

    public UIServerStub(TestSettingsBuilder settings, int port) {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new UIConnectorGrpc.UIConnectorImplBase() {
                        @Override
                        public void requestSettings(SettingsRequest request, StreamObserver<SettingsResponse> responseObserver) {
                            responseObserver.onNext(SettingsResponse
                                    .newBuilder()
                                    .setMayStartTracing(true)
                                    .setShouldTraceIdentityHashCode(false)
                                    .setTraceCollections(settings.getTraceCollections())
                                    .setTraceStartMethod(new MethodMatcher(settings.getMainClassName(), settings.getMethodToTrace()).toString())
                                    .setTracePackages(settings.getPackages())
                                    .build());
                            responseObserver.onCompleted();
                        }

                        @Override
                        public void uploadCallGraph(TCallTraceLogUploadRequest request, StreamObserver<TCallTraceLogUploadResponse> responseObserver) {
                            future.complete(request);
                            responseObserver.onCompleted();
                        }
                    })
                    .executor(executorService)
                    .build()
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TCallTraceLogUploadRequest get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(timeout, unit);
    }

    @Override
    public void close() throws Exception {
        server.shutdownNow();
        server.awaitTermination(1, TimeUnit.MINUTES);

        executorService.shutdownNow();
    }
}
