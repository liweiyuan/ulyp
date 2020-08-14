package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.StringRepresentation;
import javafx.scene.text.Text;

public class FxStringObjectValue extends FxObjectValue {

    FxStringObjectValue(StringRepresentation representation, ClassDescription classDescription) {
        super(classDescription);
        Text text = new TrimmedText(representation.print());
        text.getStyleClass().add("ulyp-ctt-string");

        super.getChildren().add(text);
    }
}
