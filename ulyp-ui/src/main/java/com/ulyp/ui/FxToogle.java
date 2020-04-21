package com.ulyp.ui;

import javafx.scene.control.Button;
import javafx.scene.paint.Paint;

public class FxToogle {

    private final String text;
    private final Button button;

    // TODO remove volatile and have a proper Settings model
    // Must be volatile since it's being read in GRPC service thread
    private volatile boolean value = true;

    public FxToogle(String text, Button button, boolean defaultValue) {
        this.text = text;
        this.button = button;
        setValue(defaultValue);
    }

    public boolean getValue() {
        return value;
    }

    private void setValue(boolean value) {
        this.value = value;
        render();
    }

    public void switchValue() {
        value = !value;
        render();
    }

    public void render() {
        if (value) {
            button.setTextFill(Paint.valueOf("#00ff00"));
            button.setText(text + ": ON");
        } else {
            button.setTextFill(Paint.valueOf("#ff0000"));
            button.setText(text + ": OFF");
        }
    }
}
