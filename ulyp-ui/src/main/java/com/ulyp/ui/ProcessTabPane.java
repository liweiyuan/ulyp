package com.ulyp.ui;

import com.ulyp.ui.code.SourceCodeView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProcessTabPane extends TabPane {

    private final Map<String, ProcessTab> processesByMainClass = new HashMap<>();

    public ProcessTabPane() {
    }

    public void clear() {
        for (Tab tab : getTabs()) {
            ProcessTab processTab = (ProcessTab) tab;
            processTab.dispose();
        }
        getTabs().clear();
        processesByMainClass.clear();
    }

    public ProcessTab getSelectedTab() {
        return (ProcessTab) getSelectionModel().getSelectedItem();
    }

    @NotNull
    public ProcessTab getOrCreateProcessTab(String mainClassName, SourceCodeView sourceCodeView) {
        ProcessTab processTab = processesByMainClass.get(mainClassName);
        if (processTab == null) {
            processesByMainClass.put(mainClassName, processTab = new ProcessTab(this, sourceCodeView, mainClassName));
            getTabs().add(processTab);
        }
        return processTab;
    }
}
