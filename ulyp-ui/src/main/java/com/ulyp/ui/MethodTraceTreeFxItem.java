package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class MethodTraceTreeFxItem extends TreeItem<Node> {

    private final RenderSettings renderSettings;
    private final MethodTraceTreeNode node;
    private final int totalNodeCount;

    private boolean loaded = false;

    public MethodTraceTreeFxItem(MethodTraceTreeNode node, RenderSettings renderSettings, int totalNodeCountInTree) {
        super(MethodTraceTreeRenderer.render(node, renderSettings, totalNodeCountInTree));
        this.node = node;
        this.renderSettings = renderSettings;
        this.totalNodeCount = totalNodeCountInTree;
    }

    public void refresh() {
        setValue(MethodTraceTreeRenderer.render(node, renderSettings, totalNodeCount));
        if (loaded) {
            getChildren().forEach(node -> ((MethodTraceTreeFxItem) node).refresh());
        }
    }

    @Override
    public ObservableList<TreeItem<Node>> getChildren() {
        if (!loaded) {
            loadChildren();
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (!loaded) {
            loadChildren();
        }
        return super.getChildren().isEmpty();
    }

    private void loadChildren() {
        loaded = true;
        List<MethodTraceTreeFxItem> children = new ArrayList<>();
        for (MethodTraceTreeNode child : node.getChildren()) {
            children.add(new MethodTraceTreeFxItem(child, renderSettings, totalNodeCount));
        }

        super.getChildren().setAll(children);
    }

    public MethodTraceTreeNode getNode() {
        return node;
    }
}
