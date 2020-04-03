package com.ulyp.ui;

import com.ulyp.core.ClassDescriptionList;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.MethodEnterTraceList;
import com.ulyp.core.MethodExitTraceList;
import com.ulyp.storage.MethodTraceTreeNode;
import com.ulyp.storage.Storage;
import com.ulyp.storage.StoringService;
import com.ulyp.storage.inmem.InMemoryStorage;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class FXMLStackTraceViewController implements Initializable {

    public FXMLStackTraceViewController() {
    }

    public FXMLStackTraceViewController(TabPane processTabPane) {
        this.processTabPane = processTabPane;
    }

    @FXML
    public VBox primaryPane;
    @FXML
    public TabPane processTabPane;
    @FXML
    public TextField searchField;
    private ProcessTabs processTabs;
    private final Storage storage = new InMemoryStorage();
    private final RenderSettings renderSettings = new RenderSettings();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        processTabs = new ProcessTabs(processTabPane);
    }

    public void onMethodTraceTreeUploaded(TMethodTraceLogUploadRequest request) {
        MethodTraceTreeNode root = new StoringService(
                new MethodEnterTraceList(request.getTraceLog().getEnterTraces()),
                new MethodExitTraceList(request.getTraceLog().getExitTraces()),
                new MethodDescriptionList(request.getMethodDescriptionList().getData()),
                new ClassDescriptionList(request.getClassDescriptionList().getData()),
                storage).store();

        Platform.runLater(() -> addTree(request, root));
    }

    private void addTree(TMethodTraceLogUploadRequest request, MethodTraceTreeNode node) {
        ProcessTab processTab = processTabs.getOrCreateProcessTab(request.getMainClassName());
        processTab.addTree(node, renderSettings, Duration.ofMillis(request.getLifetimeMillis()));
    }

    public void clearAll(Event event) {
        processTabs.clear();
    }

    public void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            MethodTraceTreeFxItem selected = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowReturnValueClassName(true);
                renderSettings.setShowArgumentClassNames(true);
                selected.refresh();
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            MethodTraceTreeFxItem selected = processTabs.getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowReturnValueClassName(false);
                renderSettings.setShowArgumentClassNames(false);
                selected.refresh();
            }
        }
    }
}
