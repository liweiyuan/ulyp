package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class MethodTraceTreeFxItem extends TreeItem<Node> {

    private final MethodTraceTreeNode node;
    private final int totalNodeCount;

    private boolean rendered = false;

    public MethodTraceTreeFxItem(MethodTraceTreeNode node, int totalNodeCount) {
        super(MethodTraceTreeRenderer.render(node, totalNodeCount));
        this.node = node;
        this.totalNodeCount = totalNodeCount;
    }

    @Override
    public ObservableList<TreeItem<Node>> getChildren() {
        if (!rendered) {
            loadChildren();
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (!rendered) {
            loadChildren();
        }
        return super.getChildren().isEmpty();
    }

    private void loadChildren() {
        rendered = true;
        List<MethodTraceTreeFxItem> children = new ArrayList<>();
        for (MethodTraceTreeNode child : node.getChildren()) {
            children.add(new MethodTraceTreeFxItem(child, totalNodeCount));
        }

        super.getChildren().setAll(children);
    }
}
