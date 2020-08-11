package com.ulyp.ui.server;

import com.ulyp.core.util.CommaSeparatedList;
import com.ulyp.transport.*;
import com.ulyp.ui.FxController;
import io.grpc.stub.StreamObserver;
import javafx.scene.control.Slider;

import java.util.Arrays;

public class UIConnectorServiceImpl extends UiTransportGrpc.UiTransportImplBase {

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

        response.addAllInstrumentedPackages(
                Arrays.asList(viewController.getInstrumentedPackagesTextField().getText().split(","))
        );
        response.addAllExcludedFromInstrumentationPackages(
                Arrays.asList(viewController.getExcludedFromInstrumentationPackagesTextField().getText().split(","))
        );
        response.addAllTraceStartMethods(CommaSeparatedList.parse(viewController.getStartMethodTextField().getText()));

        // turned off now
        response.setShouldTraceIdentityHashCode(false);

        Slider slider = viewController.getTracingPrecisionSlider();
        response.setTraceCollections(Double.compare(viewController.getTracingPrecisionSlider().getValue(), slider.getMax()) == 0);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
