package com.ulyp.ui.renderers;

import com.ulyp.core.printers.UnknownTypeInfo;
import javafx.scene.text.Text;

import java.util.Arrays;

public class RenderedThrownUnknownObject extends RenderedObject {

    public RenderedThrownUnknownObject() {
        super(UnknownTypeInfo.getInstance());

        super.getChildren().addAll(
                Arrays.asList(
                        new Text("?")
                )
        );
    }
}
