package com.ulyp.ui;

import com.ulyp.ui.code.SourceCodeView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CallRecordTreePrimaryView {

    private final TabPane processTabPane;
    private final SourceCodeView sourceCodeView;
    private final Map<String, ProcessTab> processesByMainClass = new HashMap<>();

    public CallRecordTreePrimaryView(TabPane processTabPane, SourceCodeView sourceCodeView) {
        this.processTabPane = processTabPane;
        this.sourceCodeView = sourceCodeView;
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
            processesByMainClass.put(mainClassName, processTab = new ProcessTab(processTabPane, sourceCodeView, mainClassName));
            processTabPane.getTabs().add(processTab);
        }
        return processTab;
    }
}
