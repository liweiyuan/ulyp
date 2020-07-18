package com.ulyp.ui;

import com.ulyp.core.ClassDescriptionList;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.CallEnterTraceList;
import com.ulyp.core.CallExitTraceList;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import com.ulyp.core.CallGraphDatabase;
import com.ulyp.core.CallGraphDao;
import com.ulyp.core.heap.HeapCallGraphDatabase;
import com.ulyp.transport.TCallTraceLogUploadRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

public class FxController implements Initializable {

    public FxController() {
    }

    @FXML
    public VBox primaryPane;
    @FXML
    public TabPane processTabPane;
    @FXML
    public TextField searchField;
    @FXML
    public ToggleButton traceSwitchButton;
    @FXML
    public Slider tracingPrecisionSlider;

    private ProcessTabs processTabs;
    private final CallGraphDatabase callGraphDatabase = new HeapCallGraphDatabase();
    private final RenderSettings renderSettings = new RenderSettings();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        processTabs = new ProcessTabs(callGraphDatabase, processTabPane);
    }

    public void onCallTraceTreeUploaded(TCallTraceLogUploadRequest request) {
        CallTraceTree tree = new CallGraphDao(
                new CallEnterTraceList(request.getTraceLog().getEnterTraces()),
                new CallExitTraceList(request.getTraceLog().getExitTraces()),
                new MethodDescriptionList(request.getMethodDescriptionList().getData()),
                new ClassDescriptionList(request.getClassDescriptionList().getData()),
                callGraphDatabase).getCallTraceTree();

        Platform.runLater(() -> addTree(request, tree));
    }

    private void addTree(TCallTraceLogUploadRequest request, CallTraceTree tree) {
        ProcessTab processTab = processTabs.getOrCreateProcessTab(request.getMainClassName());
        processTab.addTree(tree, renderSettings, Duration.ofMillis(request.getLifetimeMillis()));
    }

    public void clearAll(Event event) {
        processTabs.clear();
    }

    public void onSearchActivated(KeyEvent event) {

    }

    public void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            FxCallTrace selected = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
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
                    FxCallTrace selectedCallTrace = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
                    if (selectedCallTrace != null) {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        CallTrace callTrace = selectedCallTrace.getNode();
                        content.putString(callTrace.getClassName() + "." + callTrace.getMethodName());
                        clipboard.setContent(content);
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            FxCallTrace selected = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowReturnValueClassName(false);
                renderSettings.setShowArgumentClassNames(false);
                selected.refresh();
            }
        }
    }

    public ToggleButton getFxTracingSwitch() {
        return traceSwitchButton;
    }

    public Slider getTracingPrecisionSlider() {
        return tracingPrecisionSlider;
    }
}