package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import javafx.scene.text.Text;

public class RenderedNull extends RenderedObject {

    private static final RenderedNull INSTANCE = new RenderedNull();

    public static RenderedNull getInstance() {
        return INSTANCE;
    }

    RenderedNull() {
        super(ClassDescription.UNKNOWN);

        super.getChildren().add(new Text("null"));
    }
}
