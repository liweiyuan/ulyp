package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.StringRepresentation;
import javafx.scene.text.TextFlow;

public abstract class FxObjectValue extends TextFlow {

    private final ClassDescription type;

    protected FxObjectValue(ClassDescription type) {
        this.type = type;
    }

    public static FxObjectValue of(ObjectRepresentation repr) {
        FxObjectValue objectValue;

        if (repr instanceof StringRepresentation) {

            objectValue = new FxStringObjectValue((StringRepresentation) repr, repr.getType());
        } else {

            objectValue = new FxPlainObjectValue(repr, repr.getType());
        }

        objectValue.getChildren().forEach(node -> {
            node.getStyleClass().add("ulyp-ctt");
            node.getStyleClass().add("ulyp-ctt-object-repr");
        });
        return objectValue;
    }
}
