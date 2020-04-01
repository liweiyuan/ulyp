package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.time.Duration;
import java.util.function.Consumer;

public class ProcessTab extends Tab {
    private final MethodTraceTreeList tabList;

    ProcessTab(TabPane processTabPane, String mainClassName, Consumer<Event> onClose) {
        super(mainClassName);
        TabPane tabPane = new TabPane();
        this.tabList = new MethodTraceTreeList(tabPane, onClose);
        setContent(tabPane);
        tabPane.prefHeightProperty().bind(processTabPane.heightProperty());
        tabPane.prefWidthProperty().bind(processTabPane.widthProperty());
    }

    public void addMttTree(MethodTraceTreeNode tree, RenderSettings renderSettings, Duration lifetime) {
        tabList.add(tree, renderSettings, lifetime);
    }
}
