package com.ulyp.ui;

public class RenderSettings {

    private boolean showArgumentClassNames = false;
    private boolean showReturnValueClassName = false;

    public boolean showsArgumentClassNames() {
        return showArgumentClassNames;
    }

    public RenderSettings setShowArgumentClassNames(boolean showArgumentClassNames) {
        this.showArgumentClassNames = showArgumentClassNames;
        return this;
    }

    public boolean  showsReturnValueClassName() {
        return showReturnValueClassName;
    }

    public RenderSettings setShowReturnValueClassName(boolean showReturnValueClassName) {
        this.showReturnValueClassName = showReturnValueClassName;
        return this;
    }
}
