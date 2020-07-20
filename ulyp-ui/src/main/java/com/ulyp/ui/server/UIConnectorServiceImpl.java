package com.ulyp.ui.server;

import com.ulyp.transport.*;
import com.ulyp.ui.FxController;
import io.grpc.stub.StreamObserver;
import javafx.scene.control.Slider;

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

        response.setTracePackages(viewController.getTracePackagesTextField().getText());
        response.setTraceStartMethod(viewController.getStartMethodTextField().getText());

        // turned off now
        response.setShouldTraceIdentityHashCode(false);

        Slider slider = viewController.getTracingPrecisionSlider();
        response.setTraceCollections(Double.compare(viewController.getTracingPrecisionSlider().getValue(), slider.getMax()) == 0);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
