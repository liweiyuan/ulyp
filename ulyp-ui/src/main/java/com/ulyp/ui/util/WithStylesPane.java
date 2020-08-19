package com.ulyp.ui.util;

import javafx.scene.layout.Pane;

import java.util.Arrays;

public class WithStylesPane<T extends Pane> {

    private final T pane;

    public WithStylesPane(T pane, String... styles) {
        pane.getChildren().forEach(child -> Arrays.stream(styles).forEach(style -> child.getStyleClass().add(style)));

        this.pane = pane;
    }

    public T get() {
        return pane;
    }
}
