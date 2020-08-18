package com.ulyp.ui;

import com.ulyp.core.CallRecordDatabase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CallRecordTreePrimaryView {

    private final CallRecordDatabase database;
    private final TabPane processTabPane;
    private final Map<String, ProcessTab> processesByMainClass = new HashMap<>();

    public CallRecordTreePrimaryView(CallRecordDatabase database, TabPane processTabPane) {
        this.database = database;
        this.processTabPane = processTabPane;
    }

    public void clear() {
        // TODO there should be a proper way to do that
        for (Tab tab : processTabPane.getTabs()) {
            ProcessTab processTab = (ProcessTab) tab;
            processTab.dispose();
        }
        processTabPane.getTabs().clear();
        processesByMainClass.clear();
    }

    public ProcessTab getSelectedTab() {
        return (ProcessTab) processTabPane.getSelectionModel().getSelectedItem();
    }

    @NotNull
    public ProcessTab getOrCreateProcessTab(String mainClassName) {
        ProcessTab processTab = processesByMainClass.get(mainClassName);
        if (processTab == null) {
            processesByMainClass.put(mainClassName, processTab = new ProcessTab(database, processTabPane, mainClassName));
            processTabPane.getTabs().add(processTab);
        }
        return processTab;
    }
}
