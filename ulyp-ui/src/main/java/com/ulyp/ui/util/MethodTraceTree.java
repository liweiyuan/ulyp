package com.ulyp.ui.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class MethodTraceTree {

    private final MethodTraceTreeNode root;

    public MethodTraceTree(MethodTraceTreeNode root) {
        this.root = root;
    }

    public MethodTraceTreeNode getRoot() {
        return root;
    }

    public void doBfs(Consumer<MethodTraceTreeNode> consumer) {
        Queue<MethodTraceTreeNode> queue = new LinkedList<>();
        queue.add(root);

        while(!queue.isEmpty()) {
            MethodTraceTreeNode node = queue.poll();
            consumer.accept(node);
            queue.addAll(node.getChildren());
        }
    }

    public SearchIndex getSearchIndex() {
        SearchIndex[] keeper = new SearchIndex[]{ HashSetIndex.empty() };
        doBfs(node -> keeper[0] = keeper[0].mergeWith(node.getSearchIndex()));
        return keeper[0];
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
