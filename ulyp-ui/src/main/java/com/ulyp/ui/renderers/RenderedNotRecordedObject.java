package com.ulyp.ui.renderers;

import com.ulyp.core.printers.UnknownTypeInfo;
import javafx.scene.text.Text;

public class RenderedNotRecordedObject extends RenderedObject {

    protected RenderedNotRecordedObject() {
        super(UnknownTypeInfo.getInstance());

        super.getChildren().add(new Text("?"));
    }
}
