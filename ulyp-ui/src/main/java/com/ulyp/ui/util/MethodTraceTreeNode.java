package com.ulyp.ui.util;

import com.google.protobuf.ProtocolStringList;
import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodInfo;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodTraceTreeNode {

    private final MethodTraceTreeNode parent;
    private final TMethodEnterTrace methodEnterTrace;
    private TMethodExitTrace methodExitTrace;
    private final TMethodInfo methodInfo;
    private final List<MethodTraceTreeNode> children;
    private String resultCache = null;

    public MethodTraceTreeNode(MethodTraceTreeNode parent, TMethodEnterTrace methodEnterTrace, TMethodInfo methodInfo) {
        this.parent = parent;
        this.methodEnterTrace = methodEnterTrace;
        this.methodInfo = methodInfo;
        this.children = new ArrayList<>();
    }

    public MethodTraceTreeNode getParent() {
        return parent;
    }

    public long getCallId() {
        return methodEnterTrace.getCallId();
    }

    public TMethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodExitTrace(TMethodExitTrace methodExitTrace) {
        this.methodExitTrace = methodExitTrace;
    }

    public List<MethodTraceTreeNode> getChildren() {
        return children;
    }

    public void addChild(MethodTraceTreeNode node) {
        children.add(node);
    }

    public boolean matchesTo(MethodMatcher methodMatcher) {
        return methodMatcher.matchesExact(this.methodInfo.getClassName(), this.getMethodInfo().getMethodName());
    }

    public TextFlow toTextFlow() {
        StringBuilder builder = new StringBuilder(1024 * 10);
        builder.append(getResult()).append(" : ");
        Text returnValueText = new Text(builder.toString());
        if (!methodExitTrace.getThrown().isEmpty()) {
            returnValueText.setFill(Color.RED);
        }

        builder.setLength(0);
        builder.append(StringUtils.toSimpleName(methodInfo.getClassName()))
                .append(".")
                .append(methodInfo.getMethodName());

        Text methodNameText = new Text(builder.toString());
        methodNameText.setStyle("-fx-font-weight: bold");

        builder.setLength(0);
        builder.append("(");
        appendArgs(builder);
        builder.append(")");

        Text methodParamsText = new Text(builder.toString());

        return new TextFlow(returnValueText, methodNameText, methodParamsText);
    }

    private void appendArgs(StringBuilder builder) {
        ProtocolStringList args = methodEnterTrace.getArgsList();
        for (int i = 0; i < args.size(); i++) {
            builder.append(args.get(i));
            if (i < args.size() - 1) {
                builder.append(", ");
            }
        }
    }

    public SearchIndex getSearchIndex() {
        Set<String> words = new HashSet<>(methodEnterTrace.getArgsList());
        if (methodInfo.getReturnsSomething() && !methodExitTrace.getReturnValue().isEmpty()) {
            words.add(methodExitTrace.getReturnValue());
        }
        words.add(methodInfo.getMethodName());
        words.add(StringUtils.toSimpleName(methodInfo.getClassName()));
        return new HashSetIndex(words);
    }

    private String getResult() {
        if (resultCache != null) {
            return resultCache;
        }

        if (methodInfo == null || methodExitTrace == null) {
            return resultCache = "???";
        }

        if (!methodExitTrace.getThrown().isEmpty()) {
            return resultCache = resultToString(methodExitTrace.getThrown());
        } else {
            return resultCache = (
                    methodInfo.getReturnsSomething() && !methodExitTrace.getReturnValue().isEmpty()
                    ? resultToString(methodExitTrace.getReturnValue())
                    : "void"
            );
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getResult()).append(" : ");
        builder.append(methodInfo.getClassName())
                .append(".")
                .append(methodInfo.getMethodName());
        builder.append("(");
        appendArgs(builder);
        builder.append(")");
        return builder.toString();
    }

    private String resultToString(Object str) {
        if (str != null) {
            String input = (String) str;
            if (input.length() < 100) {
                return input;
            }
            StringBuilder output = new StringBuilder(input.length() + 10);
            for (int i = 0; i < input.length(); i++) {
                if (i % 100 == 0) {
                    output.append("\n");
                }
                output.append(input.charAt(i));
            }
            return output.toString();
        } else {
            return "null";
        }
    }
}
