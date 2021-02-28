package com.ulyp.ui.renderers;

import com.ulyp.core.printers.Printable;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.ui.RenderSettings;
import javafx.scene.text.Text;

// TODO retire
public class RenderedPlainObject extends RenderedObject {

    RenderedPlainObject(Printable printable, TypeInfo classDescription, RenderSettings renderSettings) {
        super(classDescription);
        Text text = new MultilinedText(printable.print());
        super.getChildren().add(text);
    }
}
