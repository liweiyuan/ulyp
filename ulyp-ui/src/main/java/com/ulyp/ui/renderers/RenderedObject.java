package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.NullObject;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.StringObject;
import javafx.scene.text.TextFlow;

public abstract class RenderedObject extends TextFlow {

    private final ClassDescription type;

    protected RenderedObject(ClassDescription type) {
        this.type = type;
    }

    public static RenderedObject of(ObjectRepresentation repr) {
        RenderedObject objectValue;

        if (repr instanceof StringObject) {

            objectValue = new RenderedStringObject((StringObject) repr, repr.getType());
        } else if (repr instanceof NullObject) {

            objectValue = RenderedNull.getInstance();
        } else {

            objectValue = new RenderedPlainObject(repr, repr.getType());
        }

        objectValue.getChildren().forEach(node -> {
            node.getStyleClass().add("ulyp-ctt");
            node.getStyleClass().add("ulyp-ctt-object-repr");
        });
        return objectValue;
    }
}
