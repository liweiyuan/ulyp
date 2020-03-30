package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import com.ulyp.ui.util.StringUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MethodTraceTreeRenderer {

    public static Node render(MethodTraceTreeNode node, int totalNodeCountInTree) {
        List<Text> text = new ArrayList<>();

        text.add(renderReturnValue(node));
        text.add(renderMethodName(node));
        text.addAll(renderArguments(node));

        Rectangle rect = new Rectangle(600.0 * node.getSubtreeNodeCount() / totalNodeCountInTree,20, Paint.valueOf("#efefef"));
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.getChildren().addAll(rect, new TextFlow(text.toArray(new Text[0])));

        return stack;
    }

    private static List<Text> renderArguments(MethodTraceTreeNode node) {
        boolean hasParameterNames = !node.getParameterNames().isEmpty() && node.getParameterNames().stream().noneMatch(name -> name.startsWith("arg"));

        List<Text> output = new ArrayList<>();
        output.add(new Text("("));
        for (int i = 0; i < node.getArgs().size(); i++) {
            if (hasParameterNames) {
                output.add(new Text(node.getParameterNames().get(i)));
                output.add(new Text(": "));
                output.add(new Text(node.getArgs().get(i)));
            } else {
                output.add(new Text(node.getArgs().get(i)));
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
    private static Text renderReturnValue(MethodTraceTreeNode node) {
        Text returnValueText = new Text(trimText(node.getResult()) + " : ");
        if (node.hasThrown()) {
            returnValueText.setFill(Color.RED);
        }
        return returnValueText;
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
