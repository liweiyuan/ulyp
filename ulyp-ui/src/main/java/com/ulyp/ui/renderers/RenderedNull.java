package com.ulyp.ui.renderers;

import com.ulyp.core.printers.UnknownTypeInfo;
import javafx.scene.text.Text;

public class RenderedNull extends RenderedObject {

    RenderedNull() {
        super(UnknownTypeInfo.getInstance());

        super.getChildren().add(new Text("null"));
    }
}
