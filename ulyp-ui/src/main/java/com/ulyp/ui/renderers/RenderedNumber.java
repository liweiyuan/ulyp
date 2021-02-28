package com.ulyp.ui.renderers;

import com.ulyp.core.printers.NumberObjectRepresentation;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.ui.RenderSettings;
import com.ulyp.ui.util.StyledText;

import static com.ulyp.ui.util.CssClass.CALL_TREE_NUMBER;
import static com.ulyp.ui.util.CssClass.CALL_TREE_TYPE_NAME;

public class RenderedNumber extends RenderedObject {

    protected RenderedNumber(NumberObjectRepresentation numberObjectRepresentation, TypeInfo typeInfo, RenderSettings renderSettings) {
        super(typeInfo);

        if (renderSettings.showTypes()) {
            super.getChildren().add(StyledText.of(typeInfo.getName(), CALL_TREE_TYPE_NAME));
        }
        super.getChildren().add(StyledText.of(numberObjectRepresentation.getPrintedText(), CALL_TREE_NUMBER));
    }
}
