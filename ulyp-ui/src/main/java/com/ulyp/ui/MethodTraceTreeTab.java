package com.ulyp.ui;

import com.ulyp.core.MethodTraceTree;
import javafx.scene.control.Tab;

public class MethodTraceTreeTab {

    private final Tab tab;
    private final long orderStamp;

    public MethodTraceTreeTab(Tab tab, MethodTraceTree methodTree, long orderStamp) {
        this.tab = tab;
        this.orderStamp = orderStamp;
    }

    public Tab getJavaFxTab() {
        return tab;
    }

    public long getOrderStamp() {
        return orderStamp;
    }
}
