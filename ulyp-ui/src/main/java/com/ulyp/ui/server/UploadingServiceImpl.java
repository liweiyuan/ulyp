package com.ulyp.ui.server;

import com.ulyp.transport.TMethodTraceLogUploadRequest;
import com.ulyp.transport.TMethodTraceLogUploadResponse;
import com.ulyp.transport.UploadingServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.function.Consumer;

public class UploadingServiceImpl extends UploadingServiceGrpc.UploadingServiceImplBase {

    private final Consumer<TMethodTraceLogUploadRequest> requestProcessor;

    public UploadingServiceImpl(Consumer<TMethodTraceLogUploadRequest> requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void upload(TMethodTraceLogUploadRequest request, StreamObserver<TMethodTraceLogUploadResponse> responseObserver) {
        requestProcessor.accept(request);
        responseObserver.onNext(TMethodTraceLogUploadResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
