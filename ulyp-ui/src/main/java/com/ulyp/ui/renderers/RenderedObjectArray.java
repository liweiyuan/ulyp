package com.ulyp.ui.renderers;

import com.ulyp.core.printers.ObjectArrayRepresentation;
import com.ulyp.ui.util.StyledText;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RenderedObjectArray extends RenderedObject {

    protected RenderedObjectArray(ObjectArrayRepresentation arrayRepresentation) {
        super(arrayRepresentation.getType());

        List<RenderedObject> renderedObjects = arrayRepresentation.getRecordedItems()
                .stream()
                .map(RenderedObject::of)
                .collect(Collectors.toList());

        List<Node> texts = new ArrayList<>();

        texts.add(StyledText.of("[", "ulyp-ctt-sep"));

        for (int i = 0; i < renderedObjects.size(); i++) {
            texts.add(renderedObjects.get(i));
            if (i != renderedObjects.size() - 1 || renderedObjects.size() < arrayRepresentation.getLength()) {
                texts.add(StyledText.of(", ", "ulyp-ctt-sep"));
            }
        }

        if (renderedObjects.size() < arrayRepresentation.getLength()) {
            texts.add(StyledText.of((arrayRepresentation.getLength() - renderedObjects.size()) + " more...", "ulyp-ctt-sep"));
        }

        texts.add(StyledText.of("]", "ulyp-ctt-sep"));

        super.getChildren().addAll(texts);
    }
}
