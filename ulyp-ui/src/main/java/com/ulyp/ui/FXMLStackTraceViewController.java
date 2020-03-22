package com.ulyp.ui;

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
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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

    private final Storage storage = new InMemoryStorage();

    private final StoringService storingService = new StoringService(storage);

    public Map<String, ProcessTab> processesByMainClass = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void onMethodTraceTreeUploaded(TMethodTraceLogUploadRequest request) {
        MethodTraceTreeNode root = storingService.store(
                new MethodEnterTraceList(request.getTraceLog().getEnterTraces()),
                new MethodExitTraceList(request.getTraceLog().getExitTraces()),
                new MethodDescriptionList(request.getMethodDescriptionList().getData())
        );

        Platform.runLater(() -> addTree(request, root));
    }

    @NotNull
    private ProcessTab getOrCreateProcessTab(String mainClassName) {
        ProcessTab processTab = processesByMainClass.get(mainClassName);
        if (processTab == null) {
            Consumer<Event> closer = ev -> {
                ProcessTab processTabToRemove = processesByMainClass.remove(mainClassName);
                processTabPane.getTabs().remove(processTabToRemove.getTab());
            };

            processesByMainClass.put(mainClassName, processTab = new ProcessTab(processTabPane, mainClassName, closer));
            processTabPane.getTabs().add(processTab.getTab());
        }
        return processTab;
    }

    private void addTree(TMethodTraceLogUploadRequest request, MethodTraceTreeNode node) {
        ProcessTab processTab = getOrCreateProcessTab(request.getMainClassName());
        processTab.getTabList().add(node, Duration.ofMillis(request.getLifetimeMillis()));
    }

    public void clearAll(Event event) {
        processTabPane.getTabs().clear();
        processesByMainClass.clear();
    }

    public void tabPaneKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.D) {
            for (ProcessTab processTab : processesByMainClass.values()) {
                if (processTab.getTab().isSelected()) {
                    if (event.getCode() == KeyCode.A) {
                        processTab.getTabList().selectNextLeftTab();
                    } else {
                        processTab.getTabList().selectNextRightTab();
                    }
                    return;
                }
            }
        }
    }

    public void onKeyReleased(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) {
            return;
        }

        processesByMainClass.values().forEach(
                processTab -> processTab.getTabList().applySearch(searchField.getText().trim())
        );
    }
}
