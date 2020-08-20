package com.ulyp.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CallRecordTreePrimaryView {

    private final TabPane processTabPane;
    private final Map<String, ProcessTab> processesByMainClass = new HashMap<>();

    public CallRecordTreePrimaryView(TabPane processTabPane) {
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
            processesByMainClass.put(mainClassName, processTab = new ProcessTab(processTabPane, mainClassName));
            processTabPane.getTabs().add(processTab);
        }
        return processTab;
    }
}
