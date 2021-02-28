package com.ulyp.ui;

import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.ui.renderers.RenderedObject;
import com.ulyp.ui.util.ClassNameUtils;
import com.ulyp.ui.util.TextBuilder;
import com.ulyp.ui.util.WithStylesPane;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderedCallRecord extends TextFlow {

    public RenderedCallRecord(CallRecord node, RenderSettings renderSettings) {
        List<Node> text = new ArrayList<>();

        text.addAll(renderReturnValue(node, renderSettings));
        text.addAll(renderMethodName(node));
        text.addAll(renderArguments(node, renderSettings));

        getChildren().addAll(text);
    }

    private static TextBuilder text() {
        return new TextBuilder().style("ulyp-ctt");
    }

    private static List<Node> renderReturnValue(CallRecord node, RenderSettings renderSettings) {
        if (!node.isVoidMethod() || node.hasThrown()) {

            List<Node> output = new ArrayList<>();

            if (renderSettings.showTypes()) {
                output.add(text().text(node.getReturnValue().getType().getName()).style("ulyp-ctt-sep").build());
                output.add(text().text(": ").style("ulyp-ctt-sep").build());
            }

            RenderedObject renderedObject = new WithStylesPane<>(RenderedObject.of(node.getReturnValue()), "ulyp-ctt-return-value").get();
            if (node.hasThrown()) {
                renderedObject = new WithStylesPane<>(renderedObject, "ulyp-ctt-thrown").get();
            }

            output.add(renderedObject);
            output.add(text().text(" : ").style("ulyp-ctt-sep").build());
            return output;
        } else {
            return Collections.emptyList();
        }
    }

    private static List<Node> renderArguments(CallRecord node, RenderSettings renderSettings) {
        boolean hasParameterNames = !node.getParameterNames().isEmpty() && node.getParameterNames().stream().noneMatch(name -> name.startsWith("arg"));

        List<Node> output = new ArrayList<>();
        output.add(text().text("(").style("ulyp-ctt-sep").build());

        for (int i = 0; i < node.getArgs().size(); i++) {
            ObjectRepresentation argValue = node.getArgs().get(i);
            if (renderSettings.showTypes()) {
                output.add(text().text(argValue.getType().getName()).style("ulyp-ctt-sep").build());
                output.add(text().text(": ").style("ulyp-ctt-sep").build());
            }

            if (hasParameterNames) {
                output.add(text().text(node.getParameterNames().get(i)).style("ulyp-ctt-arg-name").build());
                output.add(text().text(": ").style("ulyp-ctt-sep").build());
            }

            output.add(RenderedObject.of(argValue));

            if (i < node.getArgs().size() - 1) {
                output.add(text().text(", ").style("ulyp-ctt-sep").build());
            }
        }

        output.add(text().text(")").style("ulyp-ctt-sep").build());
        return output;
    }

    @NotNull
    private static List<Node> renderMethodName(CallRecord node) {
        List<Node> result = new ArrayList<>();

        if (node.isStatic() || node.isConstructor()) {
            result.add(text().text(ClassNameUtils.toSimpleName(node.getClassName())).style("ulyp-ctt-method-name").build());
        } else {
            RenderedObject callee = RenderedObject.of(node.getCallee());
            callee.getChildren().forEach(child -> child.getStyleClass().add("ulyp-ctt-callee"));
            result.add(callee);
        }
        result.add(text().text(".").style("ulyp-ctt-method-name").build());

        TextBuilder methodNameBuilder = text().text(node.getMethodName()).style("ulyp-ctt-method-name");
        if (node.isStatic()) {
            methodNameBuilder = methodNameBuilder.style("ulyp-ctt-static-method-name");
        }
        result.add(methodNameBuilder.build());
        return result;
    }
}
