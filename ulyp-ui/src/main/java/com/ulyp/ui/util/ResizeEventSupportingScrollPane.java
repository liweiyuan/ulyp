package com.ulyp.ui.util;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class ResizeEventSupportingScrollPane extends ScrollPane {

    private final List<ResizeEventListener> listeners = new ArrayList<>();

    public ResizeEventSupportingScrollPane(Node content) {
        super(content);

        this.setOnKeyPressed(
                key -> {
                    if (key.getCode() == KeyCode.EQUALS) {
                        listeners.forEach(listener -> listener.onResizeRequested(ResizeEvent.UP));
                    }
                    if (key.getCode() == KeyCode.MINUS) {
                        listeners.forEach(listener -> listener.onResizeRequested(ResizeEvent.DOWN));
                    }
                }
        );
    }

    public void addListener(ResizeEventListener listener) {
        listeners.add(listener);
    }
}
