package com.ulyp.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class ProcessTabPane extends TabPane {

    @Autowired
    private ApplicationContext context;

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
    public ProcessTab getOrCreateProcessTab(String mainClassName) {
        Optional<Tab> processTab = getTabs()
                .stream()
                .filter(tab -> mainClassName.equals(((ProcessTab) tab).getMainClassName()))
                .findFirst();

        if (processTab.isPresent()) {
            return (ProcessTab) processTab.get();
        } else {
            ProcessTab tab = context.getBean(ProcessTab.class, mainClassName);
            getTabs().add(tab);
            return tab;
        }
    }
}
