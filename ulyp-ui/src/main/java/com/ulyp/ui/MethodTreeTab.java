package com.ulyp.ui;

import com.ulyp.ui.util.MethodTraceTree;
import com.ulyp.ui.util.SearchIndex;
import javafx.scene.control.Tab;

public class MethodTreeTab {

    private final Tab tab;
    private final MethodTraceTree methodTree;
    private final SearchIndex searchIndex;
    private final long orderStamp;

    public MethodTreeTab(Tab tab, MethodTraceTree methodTree, SearchIndex searchIndex, long orderStamp) {
        this.tab = tab;
        this.methodTree = methodTree;
        this.searchIndex = searchIndex;
        this.orderStamp = orderStamp;
    }

    public Tab getJavaFxTab() {
        return tab;
    }

    public MethodTraceTree getMethodTree() {
        return methodTree;
    }

    public SearchIndex getSearchIndex() {
        return searchIndex;
    }

    public long getOrderStamp() {
        return orderStamp;
    }

    public String toShortString() {
        return "order=" + orderStamp + "\n" + methodTree.toString();
    }
}
