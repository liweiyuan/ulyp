package com.ulyp.ui.renderers;

import com.ulyp.core.printers.StringObjectRepresentation;
import com.ulyp.core.printers.TypeInfo;
import javafx.scene.text.Text;

public class RenderedStringObject extends RenderedObject {

    RenderedStringObject(StringObjectRepresentation representation, TypeInfo classDescription) {
        super(classDescription);
        Text text = new MultilinedText("\"" + representation.print() + "\"");
        text.getStyleClass().add("ulyp-ctt-string");

        super.getChildren().add(text);
    }
}
