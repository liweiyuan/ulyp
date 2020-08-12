package com.ulyp.ui.renderers;

import com.ulyp.core.printers.ObjectRepresentation;
import javafx.scene.text.Text;

import java.util.List;

public interface Renderer {

    List<Text> render(ObjectRepresentation representation);
}
