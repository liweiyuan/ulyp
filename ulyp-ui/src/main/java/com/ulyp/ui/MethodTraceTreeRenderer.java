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

import java.util.List;

public class MethodTraceTreeRenderer {

    public static Node render(MethodTraceTreeNode node, int totalNodeCountInTree) {
        StringBuilder builder = new StringBuilder(1024 * 10);
        builder.append(trimText(node.getResult())).append(" : ");
        Text returnValueText = new Text(builder.toString());
        if (node.hasThrown()) {
            returnValueText.setFill(Color.RED);
        }

        builder.setLength(0);
        builder.append(StringUtils.toSimpleName(node.getClassName()))
                .append(".")
                .append(node.getMethodName());

        Text methodNameText = new Text(builder.toString());
        methodNameText.setStyle("-fx-font-weight: bold");

        builder.setLength(0);
        builder.append("(");
        appendArgsTo(node, builder);
        builder.append(")");

        Text methodParamsText = new Text(builder.toString());
        methodParamsText.setFont(Font.font("monospace"));

        Rectangle rect = new Rectangle(600.0 * node.getSubtreeNodeCount() / totalNodeCountInTree,20, Paint.valueOf("#efefef"));
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.getChildren().addAll(rect, new TextFlow(returnValueText, methodNameText, methodParamsText));

        return stack;
    }

    private static void appendArgsTo(MethodTraceTreeNode node, StringBuilder builder) {
        List<String> args = node.getArgs();
        for (int i = 0; i < args.size(); i++) {
            builder.append(args.get(i));
            if (i < args.size() - 1) {
                builder.append(", ");
            }
        }
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
