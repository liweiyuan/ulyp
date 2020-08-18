package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.StringObject;
import javafx.scene.text.Text;

public class RenderedStringObject extends RenderedObject {

    RenderedStringObject(StringObject representation, ClassDescription classDescription) {
        super(classDescription);
        Text text = new MultilinedText("\"" + representation.print() + "\"");
        text.getStyleClass().add("ulyp-ctt-string");

        super.getChildren().add(text);
    }
}
