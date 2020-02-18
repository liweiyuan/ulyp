package com.ulyp.ui;

import com.google.protobuf.ProtocolStringList;
import com.ulyp.agent.transport.MethodTraceTree;
import com.ulyp.agent.transport.MethodTraceTreeNode;
import com.ulyp.ui.util.StringUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MethodTraceTreeRenderer {

    public static TreeItem<Node> renderTree(MethodTraceTree tree) {
        return renderNode(tree.getRoot(), tree.getRoot().getNodeCount());
    }

    private static TreeItem<Node> renderNode(MethodTraceTreeNode node, int totalNodeCountInTree) {
        TreeItem<Node> item = new TreeItem<>(render(node, totalNodeCountInTree));

        for (MethodTraceTreeNode child : node.getChildren()) {
            item.getChildren().add(renderNode(child, totalNodeCountInTree));
        }
        return item;
    }

    public static Node render(MethodTraceTreeNode node, int totalNodeCountInTree) {
        StringBuilder builder = new StringBuilder(1024 * 10);
        builder.append(trimText(node.getResult())).append(" : ");
        Text returnValueText = new Text(builder.toString());
        if (!node.getMethodExitTrace().getThrown().isEmpty()) {
            returnValueText.setFill(Color.RED);
        }

        builder.setLength(0);
        builder.append(StringUtils.toSimpleName(node.getMethodInfo().getClassName()))
                .append(".")
                .append(node.getMethodInfo().getMethodName());

        Text methodNameText = new Text(builder.toString());
        methodNameText.setStyle("-fx-font-weight: bold");

        builder.setLength(0);
        builder.append("(");
        appendArgsTo(node, builder);
        builder.append(")");

        Text methodParamsText = new Text(builder.toString());
        methodParamsText.setFont(Font.font("monospace"));

        Rectangle rect = new Rectangle(600.0 * node.getNodeCount() / totalNodeCountInTree,20, Paint.valueOf("#efefef"));
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.getChildren().addAll(rect, new TextFlow(returnValueText, methodNameText, methodParamsText));

        return stack;
    }

    private static void appendArgsTo(MethodTraceTreeNode node, StringBuilder builder) {
        ProtocolStringList args = node.getMethodEnterTrace().getArgsList();
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
