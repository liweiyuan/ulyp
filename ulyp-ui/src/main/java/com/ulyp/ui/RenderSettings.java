package com.ulyp.ui;

import javafx.application.Platform;
import org.springframework.stereotype.Component;

@Component
public class RenderSettings {

    private boolean showTypes = false;

    public boolean showTypes() {
        Platform.isFxApplicationThread();
        return showTypes;
    }

    public RenderSettings setShowTypes(boolean showTypes) {
        Platform.isFxApplicationThread();
        this.showTypes = showTypes;
        return this;
    }
}
