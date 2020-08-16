package com.test.cases.util;

import com.ulyp.core.util.MethodMatcher;
import com.ulyp.transport.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.*;

public class UIServerStub implements AutoCloseable {

    private final CompletableFuture<TCallRecordLogUploadRequest> future = new CompletableFuture<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Server server;

    public UIServerStub(TestSettingsBuilder settings, int port) {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new UiTransportGrpc.UiTransportImplBase() {
                        @Override
                        public void requestSettings(SettingsRequest request, StreamObserver<SettingsResponse> responseObserver) {
                            responseObserver.onNext(SettingsResponse
                                    .newBuilder()
                                    .setMayStartRecording(true)
                                    .setRecordCollectionsItems(settings.getTraceCollections())
                                    .addTraceStartMethods(new MethodMatcher(settings.getMainClassName().getSimpleName(), settings.getMethodToTrace()).toString())
                                    .addAllInstrumentedPackages(settings.getInstrumentedPackages())
                                    .addAllExcludedFromInstrumentationPackages(settings.getExcludedFromInstrumentationPackages())
                                    .build());
                            responseObserver.onCompleted();
                        }

                        @Override
                        public void uploadCallGraph(TCallRecordLogUploadRequest request, StreamObserver<TCallRecordLogUploadResponse> responseObserver) {
                            future.complete(request);
                            responseObserver.onCompleted();
                        }
                    })
                    .maxInboundMessageSize(1324 * 1024 * 1024)
                    .executor(executorService)
                    .build()
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TCallRecordLogUploadRequest get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(timeout, unit);
    }

    @Override
    public void close() throws Exception {
        server.shutdownNow();
        server.awaitTermination(1, TimeUnit.MINUTES);

        executorService.shutdownNow();
    }
}
