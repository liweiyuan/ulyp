package com.test.cases.util;

import com.ulyp.transport.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class UIServerStub implements AutoCloseable {

    private final List<TCallRecordLogUploadRequest> requests = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Server server;

    public UIServerStub(TestSettingsBuilder settings, int port) {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new UiTransportGrpc.UiTransportImplBase() {
                        @Override
                        public void requestSettings(SettingsRequest request, StreamObserver<Settings> responseObserver) {
                            responseObserver.onNext(Settings
                                    .newBuilder()
                                    .setMayStartRecording(true)
                                    .setRecordCollectionsItems(settings.getRecordCollectionItems())
                                    .addMethodsToRecord(settings.getMethodToRecord().toString())
                                    .addAllInstrumentedPackages(settings.getInstrumentedPackages())
                                    .addAllExcludedFromInstrumentationPackages(settings.getExcludedFromInstrumentationPackages())
                                    .build());
                            responseObserver.onCompleted();
                        }

                        @Override
                        public void uploadCallGraph(TCallRecordLogUploadRequest request, StreamObserver<TCallRecordLogUploadResponse> responseObserver) {
                            synchronized (requests) {
                                requests.add(request);
                            }
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

    public List<TCallRecordLogUploadRequest> getRequests() {
        synchronized (requests) {
            return new ArrayList<>(requests);
        }
    }

    @Override
    public void close() throws Exception {
        server.shutdownNow();
        server.awaitTermination(1, TimeUnit.MINUTES);

        executorService.shutdownNow();
    }
}
