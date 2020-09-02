package com.ulyp.ui;

import com.ulyp.ui.code.SourceCodeView;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProcessTabPane extends TabPane {

    public ProcessTabPane() {
    }

    public void clear() {
        for (Tab tab : getTabs()) {
            ProcessTab processTab = (ProcessTab) tab;
            processTab.dispose();
        }
        getTabs().clear();
    }

    public ProcessTab getSelectedTab() {
        return (ProcessTab) getSelectionModel().getSelectedItem();
    }

    @NotNull
    public ProcessTab getOrCreateProcessTab(String mainClassName, SourceCodeView sourceCodeView) {
        Optional<Tab> processTab = getTabs()
                .stream()
                .filter(tab -> mainClassName.equals(((ProcessTab) tab).getMainClassName()))
                .findFirst();

        if (processTab.isPresent()) {
            return (ProcessTab) processTab.get();
        } else {
            ProcessTab tab = new ProcessTab(this, sourceCodeView, mainClassName);
            getTabs().add(tab);
            return tab;
        }
    }
}
