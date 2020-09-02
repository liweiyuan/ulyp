package com.ulyp.ui;

import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.ui.renderers.RenderedObject;
import com.ulyp.ui.util.StringUtils;
import com.ulyp.ui.util.WithStylesPane;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CallRecordTreeViewRenderer {

    private static TextBuilder text() {
        return new TextBuilder().style("ulyp-ctt");
    }

    public static Node render(CallRecord node, RenderSettings renderSettings, int totalNodeCountInTree) {
        List<Node> text = new ArrayList<>();

        text.addAll(renderReturnValue(node));

        text.addAll(renderMethodName(node));
        text.addAll(renderArguments(node, renderSettings));

        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.getChildren().addAll(new CallRecordTreeNodeRelativeWeight(node, totalNodeCountInTree), new TextFlow(text.toArray(new Node[0])));

        return stack;
    }

    private static List<Node> renderReturnValue(CallRecord node) {
        if (!node.isVoidMethod() || node.hasThrown()) {
            RenderedObject renderedObject = new WithStylesPane<>(RenderedObject.of(node.getReturnValue()), "ulyp-ctt-return-value").get();
            if (node.hasThrown()) {
                renderedObject = new WithStylesPane<>(renderedObject, "ulyp-ctt-thrown").get();
            }
            return Arrays.asList(renderedObject, text().text(" : ").style("ulyp-ctt-sep").build());
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
            if (renderSettings.showsArgumentClassNames()) {
                output.add(text().text(argValue.getType().getSimpleName()).style("ulyp-ctt-arg-value").build());
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

        if (node.isStatic()) {
            result.add(text().text(StringUtils.toSimpleName(node.getClassName())).style("ulyp-ctt-method-name").build());
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
