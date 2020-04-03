package com.ulyp.ui;

import javafx.event.Event;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ProcessTabs {

    private final TabPane processTabPane;
    private final  Map<String, ProcessTab> processesByMainClass = new HashMap<>();

    public ProcessTabs(TabPane processTabPane) {
        this.processTabPane = processTabPane;
    }

    public void clear() {
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
            Consumer<Event> closer = ev -> {
                ProcessTab processTabToRemove = processesByMainClass.remove(mainClassName);
                processTabPane.getTabs().remove(processTabToRemove);
            };

            processesByMainClass.put(mainClassName, processTab = new ProcessTab(processTabPane, mainClassName, closer));
            processTabPane.getTabs().add(processTab);
        }
        return processTab;
    }
}
