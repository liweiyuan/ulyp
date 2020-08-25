package com.ulyp.ui.renderers;

import com.ulyp.core.printers.ClassObjectRepresentation;
import javafx.scene.text.Text;

public class RenderedClassObject extends RenderedObject {
    public RenderedClassObject(ClassObjectRepresentation classObject) {
        super(classObject.getType());

        Text text = new MultilinedText("class " + classObject.getCarriedType().getName());
        super.getChildren().add(text);
    }
}
