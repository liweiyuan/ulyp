package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import com.ulyp.storage.ObjectValue;
import com.ulyp.ui.util.StringUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MethodTraceTreeRenderer {

    public static Node render(MethodTraceTreeNode node, RenderSettings renderSettings, int totalNodeCountInTree) {

        List<Text> text = new ArrayList<>(
                renderReturnValue(node, renderSettings)
        );
        text.add(new Text(" : "));
        text.add(renderMethodName(node));
        text.addAll(renderArguments(node, renderSettings));

        Rectangle rect = new Rectangle(600.0 * node.getSubtreeNodeCount() / totalNodeCountInTree,20, Paint.valueOf("#efefef"));
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.getChildren().addAll(rect, new TextFlow(text.toArray(new Text[0])));

        return stack;
    }

    private static List<Text> renderArguments(MethodTraceTreeNode node, RenderSettings renderSettings) {
        boolean hasParameterNames = !node.getParameterNames().isEmpty() && node.getParameterNames().stream().noneMatch(name -> name.startsWith("arg"));

        List<Text> output = new ArrayList<>();
        output.add(new Text("("));
        for (int i = 0; i < node.getArgs().size(); i++) {
            ObjectValue argValue = node.getArgs().get(i);
            if (renderSettings.showsArgumentClassNames()) {
                Text typeName = new Text(argValue.getClassDescription().getSimpleName());
                typeName.setFill(Color.GRAY);
                output.add(typeName);
                output.add(new Text(": "));
            }

            if (hasParameterNames) {
                output.add(new Text(node.getParameterNames().get(i)));
                output.add(new Text(": "));
                output.add(new Text(argValue.getPrintedText()));
            } else {
                output.add(new Text(argValue.getPrintedText()));
            }
            if (i < node.getArgs().size() - 1) {
                output.add(new Text(", "));
            }
        }

        output.add(new Text(")"));
        return output;
    }

    @NotNull
    private static Text renderMethodName(MethodTraceTreeNode node) {
        Text methodNameText = new Text(StringUtils.toSimpleName(node.getClassName()) + "." + node.getMethodName());
        methodNameText.setStyle("-fx-font-weight: bold");
        return methodNameText;
    }

    @NotNull
    private static List<Text> renderReturnValue(MethodTraceTreeNode node, RenderSettings renderSettings) {
        List<Text> value = new ArrayList<>();

        if (renderSettings.showsReturnValueClassName()) {
            Text text = new Text(node.getReturnValue().getClassDescription().getSimpleName());
            text.setFill(Color.GRAY);
            value.add(text);
            value.add(new Text(": "));
        }
        Text returnValueText = new Text(trimText(node.getResult()));
        if (node.hasThrown()) {
            returnValueText.setFill(Color.RED);
        }
        value.add(returnValueText);
        return value;
    }

    private static String trimText(String text) {
        if (text.length() < 100) {
            return text;
        }
        StringBuilder output = new StringBuilder(text.length() + 10);
        for (int i = 0; i < text.length(); i++) {
            if (i % 100 == 0) {
                output.append("\n");
            }
            output.append(text.charAt(i));
        }
        return output.toString();
    }
}
