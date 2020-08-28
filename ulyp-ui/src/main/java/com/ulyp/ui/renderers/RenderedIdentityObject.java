package com.ulyp.ui.renderers;

import com.ulyp.core.printers.IdentityObjectRepresentation;
import com.ulyp.ui.util.StyledText;
import javafx.scene.text.Text;

import java.util.Arrays;

public class RenderedIdentityObject extends RenderedObject {

    public RenderedIdentityObject(IdentityObjectRepresentation repr) {
        super(repr.getType());

        super.getChildren().addAll(
                Arrays.asList(
                        StyledText.of(repr.getType().getSimpleName(), "ulyp-ctt-identity"),
                        StyledText.of("#", "ulyp-ctt-identity"),
                        StyledText.of(Integer.toHexString(repr.getHashCode()), "ulyp-ctt-identity")
                )
        );
    }
}
