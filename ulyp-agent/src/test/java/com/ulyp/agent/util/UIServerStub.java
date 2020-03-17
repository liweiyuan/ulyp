package com.ulyp.agent.util;

import com.ulyp.transport.TMethodTraceLogUploadRequest;
import com.ulyp.transport.TMethodTraceLogUploadResponse;
import com.ulyp.transport.UploadingServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.*;

public class UIServerStub implements AutoCloseable {

    private final CompletableFuture<TMethodTraceLogUploadRequest> reference = new CompletableFuture<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final Server server;

    public UIServerStub(int port) {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new UploadingServiceGrpc.UploadingServiceImplBase() {
                        @Override
                        public void upload(TMethodTraceLogUploadRequest request, StreamObserver<TMethodTraceLogUploadResponse> responseObserver) {
                            reference.complete(request);
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

    public TMethodTraceLogUploadRequest get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        return reference.get(timeout, unit);
    }

    @Override
    public void close() throws Exception {
        server.shutdownNow();
        server.awaitTermination(1, TimeUnit.MINUTES);

        executorService.shutdownNow();

    }
}
