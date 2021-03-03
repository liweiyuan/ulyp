package com.ulyp.ui.renderers;

import com.ulyp.core.printers.StringObjectRepresentation;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.ui.RenderSettings;
import javafx.scene.text.Text;

public class RenderedStringObject extends RenderedObject {

    RenderedStringObject(StringObjectRepresentation representation, TypeInfo classDescription, RenderSettings renderSettings) {
        super(classDescription);
        Text text = new MultilinedText("\"" + representation.getValue() + "\"");
        text.getStyleClass().add("ulyp-ctt-string");

        super.getChildren().add(text);
    }
}
