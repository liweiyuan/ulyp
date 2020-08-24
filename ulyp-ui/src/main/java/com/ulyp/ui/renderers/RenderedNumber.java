package com.ulyp.ui.renderers;

import com.ulyp.core.printers.NumberObjectRepresentation;
import com.ulyp.core.printers.TypeInfo;
import javafx.scene.text.Text;

public class RenderedNumber extends RenderedObject {

    protected RenderedNumber(NumberObjectRepresentation numberObjectRepresentation, TypeInfo typeInfo) {
        super(typeInfo);

        Text text = new Text(numberObjectRepresentation.getPrintedText());
        text.getStyleClass().add("ulyp-ctt-number");

        super.getChildren().add(text);
    }
}
