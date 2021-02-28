package com.ulyp.ui.renderers;

import com.ulyp.core.printers.IdentityObjectRepresentation;
import com.ulyp.ui.util.ClassNameUtils;
import com.ulyp.ui.util.StyledText;

import java.util.Arrays;

import static com.ulyp.ui.util.CssClass.CALL_TREE_IDENTITY_REPR;

public class RenderedIdentityObject extends RenderedObject {

    public RenderedIdentityObject(IdentityObjectRepresentation repr) {
        super(repr.getType());

        super.getChildren().addAll(
                Arrays.asList(
                        StyledText.of(ClassNameUtils.toSimpleName(repr.getType().getName()), CALL_TREE_IDENTITY_REPR),
                        StyledText.of("@", CALL_TREE_IDENTITY_REPR),
                        StyledText.of(Integer.toHexString(repr.getHashCode()), CALL_TREE_IDENTITY_REPR)
                )
        );
    }
}
