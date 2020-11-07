package com.ulyp.ui.renderers;

import com.ulyp.core.printers.*;
import javafx.scene.text.TextFlow;

public abstract class RenderedObject extends TextFlow {

    private final TypeInfo typeInfo;

    protected RenderedObject(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    public static RenderedObject of(ObjectRepresentation repr) {
        RenderedObject objectValue;

        // TODO replace with map
        if (repr instanceof StringObjectRepresentation) {
            objectValue = new RenderedStringObject((StringObjectRepresentation) repr, repr.getType());

        } else if (repr instanceof NullObjectRepresentation) {
            objectValue = new RenderedNull();

        } else if (repr instanceof NotRecordedObjectRepresentation) {

            objectValue = new RenderedNotRecordedObject();
        } else if (repr instanceof NumberObjectRepresentation) {
            objectValue = new RenderedNumber((NumberObjectRepresentation) repr, repr.getType());

        } else if (repr instanceof ObjectArrayRepresentation) {
            objectValue = new RenderedObjectArray((ObjectArrayRepresentation) repr);

        } else if (repr instanceof ClassObjectRepresentation) {
            objectValue = new RenderedClassObject((ClassObjectRepresentation) repr);
        } else if (repr instanceof IdentityObjectRepresentation) {
            objectValue = new RenderedIdentityObject((IdentityObjectRepresentation) repr);

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
