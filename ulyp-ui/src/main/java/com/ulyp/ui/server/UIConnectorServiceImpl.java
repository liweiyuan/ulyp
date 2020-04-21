package com.ulyp.ui.server;

import com.ulyp.transport.*;
import com.ulyp.ui.FxController;
import io.grpc.stub.StreamObserver;

public class UIConnectorServiceImpl extends UIConnectorGrpc.UIConnectorImplBase {

    private final FxController viewController;

    public UIConnectorServiceImpl(FxController viewController) {
        this.viewController = viewController;
    }

    @Override
    public void uploadCallGraph(TCallTraceLogUploadRequest request, StreamObserver<TCallTraceLogUploadResponse> responseObserver) {
        viewController.onCallTraceTreeUploaded(request);

        responseObserver.onNext(TCallTraceLogUploadResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestSettings(SettingsRequest request, StreamObserver<SettingsResponse> responseObserver) {
        SettingsResponse.Builder response = SettingsResponse.newBuilder();
        response.setMayStartTracing(viewController.getFxTracingSwitch().getValue());
        // todo rename
        response.setShouldTraceIdentityHashCode(viewController.getFxTraceIdentityHashCode().getValue());
        response.setTraceCollections(viewController.getFxTraceCollectionsToogle().getValue());

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
