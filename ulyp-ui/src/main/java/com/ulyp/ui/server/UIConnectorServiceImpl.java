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
    public void uploadCallGraph(TCallRecordLogUploadRequest request, StreamObserver<TCallRecordLogUploadResponse> responseObserver) {
        viewController.onCallTraceTreeUploaded(request);

        responseObserver.onNext(TCallRecordLogUploadResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestSettings(SettingsRequest request, StreamObserver<SettingsResponse> responseObserver) {
        SettingsResponse.Builder response = SettingsResponse.newBuilder();
        response.setMayStartRecording(viewController.getFxTracingSwitch().isSelected());

        response.addAllInstrumentedPackages(
                Arrays.asList(viewController.getInstrumentedPackagesTextField().getText().split(","))
        );
        response.addAllExcludedFromInstrumentationPackages(
                Arrays.asList(viewController.getExcludedFromInstrumentationPackagesTextField().getText().split(","))
        );
        response.addAllMethodsToRecord(CommaSeparatedList.parse(viewController.getStartMethodTextField().getText()));

        Slider slider = viewController.getTracingPrecisionSlider();
        response.setRecordCollectionsItems(Double.compare(viewController.getTracingPrecisionSlider().getValue(), slider.getMax()) == 0);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
