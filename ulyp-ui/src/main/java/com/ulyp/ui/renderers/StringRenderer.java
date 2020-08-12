package com.ulyp.ui.renderers;

import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.StringRepresentation;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

public class StringRenderer implements Renderer {

    public boolean supports(ObjectRepresentation representation) {
        return representation instanceof StringRepresentation;
    }

    @Override
    public List<Text> render(ObjectRepresentation representation) {
        Text text = new Text(representation.print());
        text.getStyleClass().add("ulyp-ctt-string");
        return Arrays.asList(text);
    }
}
