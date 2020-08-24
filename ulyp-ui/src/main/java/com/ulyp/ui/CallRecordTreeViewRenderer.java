package com.ulyp.ui;

import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.ui.renderers.RenderedObject;
import com.ulyp.ui.util.StringUtils;
import com.ulyp.ui.util.WithStylesPane;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
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

        text.add(text().text(" : ").style("ulyp-ctt-sep").build());

        text.addAll(renderMethodName(node));
        text.addAll(renderArguments(node, renderSettings));

        Rectangle rect = new Rectangle(600.0 * node.getSubtreeNodeCount() / totalNodeCountInTree,20);
        rect.getStyleClass().add("ulyp-asd");

        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.getChildren().addAll(rect, new TextFlow(text.toArray(new Node[0])));

        return stack;
    }

    private static List<Node> renderReturnValue(CallRecord node) {
        if (node.isVoidMethod() && !node.hasThrown()) {
            return Collections.singletonList(text().text("void").style("ulyp-ctt-sep").build());
        } else {

            RenderedObject renderedObject = new WithStylesPane<>(RenderedObject.of(node.getReturnValue()), "ulyp-ctt-return-value").get();
            if (node.hasThrown()) {
                renderedObject = new WithStylesPane<>(renderedObject, "ulyp-ctt-thrown").get();
            }
            return Collections.singletonList(renderedObject);
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

//    @NotNull
//    private static TextFlow renderReturnValue(CallRecord node, RenderSettings renderSettings) {
//        List<Text> value = new ArrayList<>();

//        if (renderSettings.showsReturnValueClassName()) {
//            value.add(text().text(node.getReturnValue().getClassDescription().getSimpleName()).style("ulyp-ctt-return-value-type").build());
//            value.add(text().text(": ").style("ulyp-ctt-sep").build());
//        }

//        Text returnValueText;
//        if (node.hasThrown()) {
//            returnValueText = text().text(trimText(node.getResult())).style("ulyp-ctt-thrown-value").build();
//        } else {
//            if (node.getReturnValue().asPrintable() instanceof IdentityObjectRepresentation) {
//
//                returnValueText = text().text(trimText(node.getResult())).style("ulyp-ctt-return-value").build();
//            } else {
//                returnValueText = text().text(trimText(node.getResult())).style("ulyp-ctt-return-value").build();
//            }
//        }
//
//        value.add(returnValueText);
//        return FxObjectValue.of(node.getReturnValue());
//    }
}
