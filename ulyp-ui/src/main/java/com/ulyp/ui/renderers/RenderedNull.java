package com.ulyp.ui.renderers;

import com.ulyp.core.printers.UnknownTypeInfo;
import com.ulyp.ui.RenderSettings;
import javafx.scene.text.Text;

public class RenderedNull extends RenderedObject {

    RenderedNull(RenderSettings renderSettings) {
        super(UnknownTypeInfo.getInstance());

        super.getChildren().add(new Text("null"));
    }
}
