package com.ulyp.ui;

import com.ulyp.core.CallRecord;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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
    public TextField instrumentedPackagesTextField;
    @FXML
    public TextField excludedFromInstrumentationPackagesTextField;
    @FXML
    public TextField startMethodTextField;
    @FXML
    public ToggleButton recordSwitchButton;
    @FXML
    public Slider recordPrecisionSlider;
    @FXML
    public SourceCodeView sourceCodeView;
    @Autowired
    public ProcessTabPane processTabPane;

    @Autowired
    private ApplicationContext context;

    Supplier<File> fileChooser;

    private final RenderSettings renderSettings = new RenderSettings();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void processRequest(TCallRecordLogUploadRequest request) {
        Platform.runLater(() -> {
            CallRecordTree tree = new CallRecordTree(request);
            ProcessTab processTab = processTabPane.getOrCreateProcessTab(tree.getProcessInfo().getMainClassName(), sourceCodeView);
            processTab.add(tree, renderSettings);
        });
    }

    public void clearAll(Event event) {
        processTabPane.clear();
    }

    public void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            CallRecordTreeNode selected = processTabPane.getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowReturnValueClassName(true);
                renderSettings.setShowArgumentClassNames(true);
                selected.refresh();
            }
        } else {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                // COPY currently selected
                ProcessTab selectedTab = processTabPane.getSelectedTab();
                if (selectedTab != null) {
                    CallRecordTreeNode selectedCallRecord = processTabPane.getSelectedTab().getSelectedTreeTab().getSelected();
                    if (selectedCallRecord != null) {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        CallRecord callRecord = selectedCallRecord.getCallRecord();
                        content.putString(callRecord.getClassName() + "." + callRecord.getMethodName());
                        clipboard.setContent(content);
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            CallRecordTreeNode selected = processTabPane.getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowReturnValueClassName(false);
                renderSettings.setShowArgumentClassNames(false);
                selected.refresh();
            }
        }
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
                TCallRecordLogUploadRequestList requests = TCallRecordLogUploadRequestList.parseFrom(inputStream);

                for (TCallRecordLogUploadRequest request : requests.getRequestList()) {
                    processRequest(request);
                }
            } catch (IOException e) {
                // TODO show error dialog
                e.printStackTrace();
            }
        }
    }
}
