package com.ulyp.ui;

import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.function.Consumer;

class ProcessTab {
    private final Tab tab;
    private final MethodTraceTreeList tabList;

    ProcessTab(TabPane processTabPane, String mainClassName, Consumer<Event> onClose) {
        this.tab = new Tab(mainClassName);
        TabPane tabPane = new TabPane();
        this.tabList = new MethodTraceTreeList(tabPane, onClose);
        tab.setContent(tabPane);
        tabPane.prefHeightProperty().bind(processTabPane.heightProperty());
        tabPane.prefWidthProperty().bind(processTabPane.widthProperty());
    }

    public Tab getTab() {
        return tab;
    }

    public MethodTraceTreeList getTabList() {
        return tabList;
    }
}
