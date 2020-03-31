package com.ulyp.ui;

public class RenderSettings {

    private boolean showArgumentClassNames = false;
    private boolean showReturnValueClassName = false;

    public boolean isShowArgumentClassNames() {
        return showArgumentClassNames;
    }

    public RenderSettings setShowArgumentClassNames(boolean showArgumentClassNames) {
        this.showArgumentClassNames = showArgumentClassNames;
        return this;
    }

    public boolean isShowReturnValueClassName() {
        return showReturnValueClassName;
    }

    public RenderSettings setShowReturnValueClassName(boolean showReturnValueClassName) {
        this.showReturnValueClassName = showReturnValueClassName;
        return this;
    }
}
