package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.NumberObject;
import javafx.scene.text.Text;

public class RenderedNumber extends RenderedObject {

    protected RenderedNumber(NumberObject numberObject, ClassDescription type) {
        super(type);

        Text text = new Text(numberObject.getPrintedText());
        text.getStyleClass().add("ulyp-ctt-number");

        super.getChildren().add(text);
    }
}
