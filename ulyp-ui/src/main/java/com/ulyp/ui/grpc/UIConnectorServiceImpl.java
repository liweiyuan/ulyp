package com.ulyp.ui.grpc;

import com.ulyp.core.util.CommaSeparatedList;
import com.ulyp.transport.*;
import com.ulyp.ui.PrimaryViewController;
import io.grpc.stub.StreamObserver;
import javafx.scene.control.Slider;

import java.util.Arrays;

public class UIConnectorServiceImpl extends UiTransportGrpc.UiTransportImplBase {

    private final PrimaryViewController viewController;

    public UIConnectorServiceImpl(PrimaryViewController viewController) {
        this.viewController = viewController;
    }

    @Override
    public void uploadCallGraph(TCallRecordLogUploadRequest request, StreamObserver<TCallRecordLogUploadResponse> responseObserver) {
        viewController.processRequest(request);

        responseObserver.onNext(TCallRecordLogUploadResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestSettings(SettingsRequest request, StreamObserver<Settings> responseObserver) {
        Settings.Builder response = Settings.newBuilder();
        response.setMayStartRecording(viewController.getFxTracingSwitch().isSelected());

        response.addAllInstrumentedPackages(
                Arrays.asList(viewController.getInstrumentedPackagesTextField().getText().split(","))
        );
        response.addAllExcludedFromInstrumentationPackages(
                Arrays.asList(viewController.getExcludedFromInstrumentationPackagesTextField().getText().split(","))
        );
        response.addAllMethodsToRecord(CommaSeparatedList.parse(viewController.getStartMethodTextField().getText()));

        Slider slider = viewController.getRecordPrecisionSlider();
        response.setRecordCollectionsItems(Double.compare(viewController.getRecordPrecisionSlider().getValue(), slider.getMax()) == 0);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
