package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import javafx.scene.text.Text;

public class RenderedNull extends RenderedObject {

    RenderedNull() {
        super(ClassDescription.UNKNOWN);

        super.getChildren().add(new Text("null"));
    }
}
