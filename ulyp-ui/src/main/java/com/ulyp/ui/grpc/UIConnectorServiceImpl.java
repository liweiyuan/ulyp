package com.ulyp.ui.grpc;

import com.ulyp.core.util.CommaSeparatedList;
import com.ulyp.transport.*;
import com.ulyp.ui.*;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UIConnectorServiceImpl extends UiTransportGrpc.UiTransportImplBase {

    private final PrimaryViewController viewController;
    private final ProcessTabPane processTabPane;
    private final RenderSettings renderSettings;

    @Autowired
    public UIConnectorServiceImpl(PrimaryViewController viewController, ProcessTabPane processTabPane, RenderSettings renderSettings) {
        this.viewController = viewController;
        this.processTabPane = processTabPane;
        this.renderSettings = renderSettings;
    }


    @Override
    public void uploadCallGraph(TCallRecordLogUploadRequest request, StreamObserver<TCallRecordLogUploadResponse> responseObserver) {
        Platform.runLater(() -> {
            CallRecordTree tree = new CallRecordTree(request);
            ProcessTab processTab = processTabPane.getOrCreateProcessTab(tree.getProcessInfo().getMainClassName());
            processTab.add(tree);
        });

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
