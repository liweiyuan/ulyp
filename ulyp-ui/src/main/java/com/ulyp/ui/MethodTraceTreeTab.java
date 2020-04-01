package com.ulyp.ui;

import javafx.scene.control.Tab;

public class MethodTraceTreeTab {

    private final Tab tab;
    private final long orderStamp;

    public MethodTraceTreeTab(Tab tab, long orderStamp) {
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
