package com.ulyp.ui;

import com.ulyp.core.CallTrace;
import com.ulyp.core.ObjectValue;
import com.ulyp.ui.renderers.FxObjectValue;
import com.ulyp.ui.util.StringUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FxCttTreeViewRenderer {

    private static TextBuilder text() {
        return new TextBuilder().style("ulyp-ctt");
    }

    public static Node render(CallTrace node, RenderSettings renderSettings, int totalNodeCountInTree) {
        List<Node> text = new ArrayList<>();


        text.add(FxObjectValue.of(node.getReturnValue()));

        text.add(text().text(" : (").style("ulyp-ctt-sep").build());

        text.add(FxObjectValue.of(node.getCallee()));

        text.add(text().text(") ").style("ulyp-ctt-sep").build());

        text.addAll(renderMethodName(node));

        text.addAll(renderArguments(node, renderSettings));

        Rectangle rect = new Rectangle(600.0 * node.getSubtreeNodeCount() / totalNodeCountInTree,20);
        rect.getStyleClass().add("ulyp-asd");

        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.getChildren().addAll(rect, new TextFlow(text.toArray(new Node[0])));

        return stack;
    }

    private static List<Node> renderArguments(CallTrace node, RenderSettings renderSettings) {
        boolean hasParameterNames = !node.getParameterNames().isEmpty() && node.getParameterNames().stream().noneMatch(name -> name.startsWith("arg"));

        List<Node> output = new ArrayList<>();
        output.add(text().text("(").style("ulyp-ctt-sep").build());

        for (int i = 0; i < node.getArgs().size(); i++) {
            ObjectValue argValue = node.getArgs().get(i);
            if (renderSettings.showsArgumentClassNames()) {
                output.add(text().text(argValue.getClassDescription().getSimpleName()).style("ulyp-ctt-arg-value").build());
                output.add(text().text(": ").style("ulyp-ctt-sep").build());
            }

            if (hasParameterNames) {
                output.add(text().text(node.getParameterNames().get(i)).style("ulyp-ctt-arg-name").build());
                output.add(text().text(": ").style("ulyp-ctt-sep").build());
            }

            output.add(FxObjectValue.of(argValue));

            if (i < node.getArgs().size() - 1) {
                output.add(text().text(", ").style("ulyp-ctt-sep").build());
            }
        }

        output.add(text().text(")").style("ulyp-ctt-sep").build());
        return output;
    }

    @NotNull
    private static List<Text> renderMethodName(CallTrace node) {
        return Arrays.asList(
                text().text(StringUtils.toSimpleName(node.getClassName())).style("ulyp-ctt-method-name").build(),
                text().text(".").style("ulyp-ctt-method-name").build(),
                text().text(node.getMethodName()).style("ulyp-ctt-method-name").build()
        );
    }

//    @NotNull
//    private static TextFlow renderReturnValue(CallTrace node, RenderSettings renderSettings) {
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
