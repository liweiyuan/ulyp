package com.ulyp.ui.renderers;

import com.ulyp.core.printers.ClassObjectRepresentation;
import com.ulyp.ui.RenderSettings;
import javafx.scene.text.Text;

public class RenderedClassObject extends RenderedObject {
    public RenderedClassObject(ClassObjectRepresentation classObject, RenderSettings renderSettings) {
        super(classObject.getType());

        Text text = new MultilinedText("class " + classObject.getCarriedType().getName());
        super.getChildren().add(text);
    }
}
