package com.ulyp.ui;

import com.ulyp.ui.util.MethodTraceTree;
import com.ulyp.ui.util.SearchIndex;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabList {

    private final List<Tab> tabs;
    private final List<SearchIndex> searchIndices;
    private final List<Long> orderStamps;
    private final List<MethodTraceTree> methodTrees;

    public TabList(List<Tab> tabs) {
        this.tabs = tabs;
        this.searchIndices = new ArrayList<>();
        this.orderStamps = new ArrayList<>();
        this.methodTrees = new ArrayList<>();
    }

    public TabList() {
        this(new ArrayList<>());
    }

    public void add(MethodTreeTab tab) {
        tabs.add(tab.getJavaFxTab());
        searchIndices.add(tab.getSearchIndex());
        orderStamps.add(tab.getOrderStamp());
        methodTrees.add(tab.getMethodTree());
    }

    public boolean isEmpty() {
        return tabs.isEmpty();
    }

    public void clear() {
        tabs.clear();
        searchIndices.clear();
        orderStamps.clear();
        methodTrees.clear();
    }

    public List<MethodTreeTab> getTabs() {
        List<MethodTreeTab> result = new ArrayList<>();
        for (int i = 0; i < tabs.size(); i++) {
            result.add(new MethodTreeTab(
                    tabs.get(i),
                    methodTrees.get(i),
                    searchIndices.get(i),
                    orderStamps.get(i))
            );
        }
        return result;
    }
}
