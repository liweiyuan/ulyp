package com.ulyp.ui.renderers;

import com.ulyp.core.printers.Printable;
import com.ulyp.core.printers.TypeInfo;
import javafx.scene.text.Text;

// TODO retire
public class RenderedPlainObject extends RenderedObject {

    RenderedPlainObject(Printable printable, TypeInfo classDescription) {
        super(classDescription);
        Text text = new MultilinedText(printable.print());
        super.getChildren().add(text);
    }
}
