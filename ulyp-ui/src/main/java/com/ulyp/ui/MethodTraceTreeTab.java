package com.ulyp.ui;

import com.ulyp.agent.transport.MethodTraceTree;
import javafx.scene.control.Tab;

public class MethodTraceTreeTab {

    private final Tab tab;
    private final MethodTraceTree methodTree;
    private final long orderStamp;

    public MethodTraceTreeTab(Tab tab, MethodTraceTree methodTree, long orderStamp) {
        this.tab = tab;
        this.methodTree = methodTree;
        this.orderStamp = orderStamp;
    }

    public Tab getJavaFxTab() {
        return tab;
    }

    public long getOrderStamp() {
        return orderStamp;
    }
}
