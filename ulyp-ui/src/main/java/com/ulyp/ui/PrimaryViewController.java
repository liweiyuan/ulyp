package com.ulyp.ui;

import com.ulyp.transport.TCallRecordLogUploadRequest;
import com.ulyp.transport.TCallRecordLogUploadRequestList;
import com.ulyp.ui.code.SourceCodeView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class PrimaryViewController implements Initializable {

    public PrimaryViewController() {
    }

    @FXML
    public VBox primaryPane;
    @FXML
    public AnchorPane processTabAnchorPane;
    @FXML
    public AnchorPane sourceCodeViewAnchorPane;
    @FXML
    public TextField instrumentedPackagesTextField;
    @FXML
    public TextField excludedFromInstrumentationPackagesTextField;
    @FXML
    public TextField startMethodTextField;
    @FXML
    public ToggleButton recordSwitchButton;
    @FXML
    public Slider recordPrecisionSlider;
    @Autowired
    public SourceCodeView sourceCodeView;
    @Autowired
    public ProcessTabPane processTabPane;

    Supplier<File> fileChooser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.processTabAnchorPane.getChildren().add(processTabPane);

        AnchorPane.setTopAnchor(processTabPane, 0.0);
        AnchorPane.setBottomAnchor(processTabPane, 0.0);
        AnchorPane.setRightAnchor(processTabPane, 0.0);
        AnchorPane.setLeftAnchor(processTabPane, 0.0);

        this.sourceCodeViewAnchorPane.getChildren().add(sourceCodeView);

        AnchorPane.setTopAnchor(sourceCodeView, 0.0);
        AnchorPane.setBottomAnchor(sourceCodeView, 0.0);
        AnchorPane.setRightAnchor(sourceCodeView, 0.0);
        AnchorPane.setLeftAnchor(sourceCodeView, 0.0);
    }

    public void clearAll(Event event) {
        processTabPane.clear();
    }

    public TextField getExcludedFromInstrumentationPackagesTextField() {
        return excludedFromInstrumentationPackagesTextField;
    }

    public TextField getInstrumentedPackagesTextField() {
        return instrumentedPackagesTextField;
    }

    public TextField getStartMethodTextField() {
        return startMethodTextField;
    }

    public ToggleButton getFxTracingSwitch() {
        return recordSwitchButton;
    }

    public Slider getRecordPrecisionSlider() {
        return recordPrecisionSlider;
    }

    public void openRecordedDump(ActionEvent actionEvent) {
        File file = fileChooser.get();
        if (file != null) {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {

                while (inputStream.available() > 0) {
                    TCallRecordLogUploadRequest request = TCallRecordLogUploadRequest.parseFrom(inputStream);

                    Platform.runLater(() -> {
                        CallRecordTreeChunk chunk = new CallRecordTreeChunk(request);
                        ProcessTab processTab = processTabPane.getOrCreateProcessTab(chunk.getProcessInfo().getMainClassName());
                        processTab.uploadChunk(chunk);
                    });
                }

            } catch (IOException e) {
                // TODO show error dialog
                e.printStackTrace();
            }
        }
    }
}
