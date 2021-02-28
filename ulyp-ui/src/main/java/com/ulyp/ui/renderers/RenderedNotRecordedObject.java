package com.ulyp.ui.renderers;

import com.ulyp.core.printers.UnknownTypeInfo;
import com.ulyp.ui.RenderSettings;
import javafx.scene.text.Text;

public class RenderedNotRecordedObject extends RenderedObject {

    protected RenderedNotRecordedObject(RenderSettings renderSettings) {
        super(UnknownTypeInfo.getInstance());

        if (renderSettings.showTypes()) {
            super.getChildren().add(new Text("Method has not yet returned any value: ?"));
        } else {
            super.getChildren().add(new Text("?"));
        }
    }
}
