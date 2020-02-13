package com.ulyp.ui;

import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabList {

    private final List<Tab> tabs;
    private final List<MethodTraceTreeTab> traceTreeTabs = new ArrayList<>();

    public TabList(List<Tab> tabs) {
        this.tabs = tabs;
    }

    public TabList() {
        this(new ArrayList<>());
    }

    public void add(MethodTraceTreeTab tab) {
        tabs.add(tab.getJavaFxTab());
        traceTreeTabs.add(tab);
    }

    public boolean isEmpty() {
        return tabs.isEmpty();
    }

    public void clear() {
        tabs.clear();
        traceTreeTabs.clear();
    }

    public List<MethodTraceTreeTab> getTabs() {
        List<MethodTraceTreeTab> result = new ArrayList<>();
        for (int i = 0; i < tabs.size(); i++) {
            result.add(traceTreeTabs.get(i));
        }
        return result;
    }
}
