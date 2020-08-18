package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.ObjectValue;
import com.ulyp.core.printers.Printable;
import com.ulyp.core.printers.StringRepresentation;
import javafx.scene.text.TextFlow;

public abstract class FxObjectValue extends TextFlow {

    private final ClassDescription type;

    protected FxObjectValue(ClassDescription type) {
        this.type = type;
    }

    public static FxObjectValue of(ObjectValue value) {
        FxObjectValue objectValue;
        Printable printable = value.asPrintable();

        if (printable instanceof StringRepresentation) {

            objectValue = new FxStringObjectValue((StringRepresentation) printable, value.getClassDescription());
        } else {

            objectValue = new FxSimpleObjectValue(printable, value.getClassDescription());
        }

        objectValue.getChildren().forEach(node -> {
            node.getStyleClass().add("ulyp-ctt");
            node.getStyleClass().add("ulyp-ctt-object-value");
        });
        return objectValue;
    }
}
