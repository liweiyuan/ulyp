package com.ulyp.ui.util;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class ResizeEventSupportingScrollPane extends ScrollPane {

    private final List<ResizeEventListener> listeners = new ArrayList<>();
    private boolean ctrlPressed = false;

    public ResizeEventSupportingScrollPane(Node content) {
        super(content);

        this.setOnKeyPressed(
                key -> {
                    if (key.getCode() == KeyCode.CONTROL) {
                        ctrlPressed = true;
                    }
                }
        );

        this.setOnKeyReleased(
                key -> {
                    if (key.getCode() == KeyCode.CONTROL) {
                        ctrlPressed = false;
                    }
                }
        );

        this.vvalueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!ctrlPressed) {
                        return;
                    }

                    double oldDouble = oldValue.doubleValue();
                    double newDouble = newValue.doubleValue();
                    if (oldDouble == 0.0 && newDouble == 0.0) {
                        return;
                    }
                    if (oldDouble == 1.0 && newDouble == 1.0) {
                        return;
                    }

                    double delta = newDouble - oldDouble;
                    if (delta > 0.0) {
                        listeners.forEach(listener -> listener.onResizeRequested(ResizeEvent.DOWN));
                    } else if (delta < 0.0) {
                        listeners.forEach(listener -> listener.onResizeRequested(ResizeEvent.UP));
                    }
                }
        );

        this.setOnScroll(ev -> {
            if (!ctrlPressed) {
                return;
            }

            if (ev.getDeltaY() > 0.0) {
                listeners.forEach(listener -> listener.onResizeRequested(ResizeEvent.UP));
            } else {
                listeners.forEach(listener -> listener.onResizeRequested(ResizeEvent.DOWN));
            }
        });
    }

    public void addListener(ResizeEventListener listener) {
        listeners.add(listener);
    }
}
