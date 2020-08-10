package com.perf.agent.benchmarks.proc;

import com.google.common.base.Strings;
import com.perf.agent.benchmarks.BenchmarkProfile;
import com.perf.agent.benchmarks.BenchmarkSettings;
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

    public UIServerStub(BenchmarkProfile benchmarkProfile) {
        try {
            server = ServerBuilder.forPort(benchmarkProfile.getUiListenPort())
                    .addService(new UiTransportGrpc.UiTransportImplBase() {
                        public void requestSettings(SettingsRequest request, StreamObserver<SettingsResponse> responseObserver) {
                            responseObserver.onNext(benchmarkProfile.getSettingsFromUi());
                            responseObserver.onCompleted();
                        }

                        public void uploadCallGraph(TCallTraceLogUploadRequest request, StreamObserver<TCallTraceLogUploadResponse> responseObserver) {
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
