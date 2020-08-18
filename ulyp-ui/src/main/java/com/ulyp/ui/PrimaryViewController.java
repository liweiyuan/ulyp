package com.ulyp.ui;

import com.ulyp.core.ClassDescriptionList;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.CallEnterRecordList;
import com.ulyp.core.CallExitRecordList;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import com.ulyp.core.CallGraphDatabase;
import com.ulyp.core.CallGraphDao;
import com.ulyp.core.heap.HeapCallGraphDatabase;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class PrimaryViewController implements Initializable {

    public PrimaryViewController() {
    }

    @FXML
    public VBox primaryPane;
    @FXML
    public TabPane processTabPane;
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

    private ProcessTabs processTabs;
    private final CallGraphDatabase callGraphDatabase = new HeapCallGraphDatabase();
    private final RenderSettings renderSettings = new RenderSettings();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        processTabs = new ProcessTabs(callGraphDatabase, processTabPane);
    }

    public void onCallRecordTreeUploaded(TCallRecordLogUploadRequest request) {
        CallRecordTree tree = new CallGraphDao(
                new CallEnterRecordList(request.getRecordLog().getEnterRecords()),
                new CallExitRecordList(request.getRecordLog().getExitRecords()),
                new MethodDescriptionList(request.getMethodDescriptionList().getData()),
                new ClassDescriptionList(request.getClassDescriptionList().getData()),
                callGraphDatabase
        ).getCallRecordTree();

        Platform.runLater(() -> addTree(request, tree));
    }

    private void addTree(TCallRecordLogUploadRequest request, CallRecordTree tree) {
        ProcessTab processTab = processTabs.getOrCreateProcessTab(request.getProcessInfo().getMainClassName());
        processTab.addTree(tree, request.getProcessInfo(), renderSettings, Duration.ofMillis(request.getLifetimeMillis()));
    }

    public void clearAll(Event event) {
        processTabs.clear();
    }

    public void onSearchActivated(KeyEvent event) {

    }

    public void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            FxCallRecord selected = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowReturnValueClassName(true);
                renderSettings.setShowArgumentClassNames(true);
                selected.refresh();
            }
        } else {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                // COPY currently selected
                ProcessTab selectedTab = processTabs.getSelectedTab();
                if (selectedTab != null) {
                    FxCallRecord selectedCallRecord = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
                    if (selectedCallRecord != null) {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        CallRecord callRecord = selectedCallRecord.getNode();
                        content.putString(callRecord.getClassName() + "." + callRecord.getMethodName());
                        clipboard.setContent(content);
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            FxCallRecord selected = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
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
}
