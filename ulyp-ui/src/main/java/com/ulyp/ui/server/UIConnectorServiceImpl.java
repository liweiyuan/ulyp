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
        response.setMayStartTracing(viewController.getFxTracingSwitch().isSelected());
        response.setShouldTraceIdentityHashCode(viewController.getFxTraceIdentityHashCode().isSelected());
        response.setTraceCollections(viewController.getFxTraceCollectionsToogle().isSelected());

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
