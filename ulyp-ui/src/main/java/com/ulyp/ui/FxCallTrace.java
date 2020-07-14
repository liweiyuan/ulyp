package com.ulyp.ui;

import com.ulyp.core.CallTrace;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class FxCallTrace extends TreeItem<Node> {

    private final RenderSettings renderSettings;
    private final CallTrace node;
    private final int totalNodeCount;

    private boolean loaded = false;

    public FxCallTrace(CallTrace node, RenderSettings renderSettings, int totalNodeCountInTree) {
        super(FxCttTreeViewRenderer.render(node, renderSettings, totalNodeCountInTree));
        this.node = node;
        this.renderSettings = renderSettings;
        this.totalNodeCount = totalNodeCountInTree;
    }

    public void refresh() {
        setValue(FxCttTreeViewRenderer.render(node, renderSettings, totalNodeCount));
        if (loaded) {
            getChildren().forEach(node -> ((FxCallTrace) node).refresh());
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
        List<FxCallTrace> children = new ArrayList<>();
        for (CallTrace child : node.getChildren()) {
            children.add(new FxCallTrace(child, renderSettings, totalNodeCount));
        }

        super.getChildren().setAll(children);
    }

    public CallTrace getNode() {
        return node;
    }
}
