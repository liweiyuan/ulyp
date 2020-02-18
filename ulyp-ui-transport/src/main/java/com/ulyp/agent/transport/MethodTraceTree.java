package com.ulyp.agent.transport;

public class MethodTraceTree {

    private final MethodTraceTreeNode root;

    public MethodTraceTree(MethodTraceTreeNode root) {
        this.root = root;
    }

    public MethodTraceTreeNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(root.toString());
        for (MethodTraceTreeNode node : root.getChildren()) {
            builder.append("\n    ").append(node.toString());
        }
        return builder.toString();
    }
}
