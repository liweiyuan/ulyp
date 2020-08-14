package com.ulyp.ui.renderers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.Printable;
import javafx.scene.text.Text;

// TODO retire
public class FxSimpleObjectValue extends FxObjectValue {

    FxSimpleObjectValue(Printable printable, ClassDescription classDescription) {
        super(classDescription);
        Text text = new Text(printable.print());
        super.getChildren().add(text);
    }
}
