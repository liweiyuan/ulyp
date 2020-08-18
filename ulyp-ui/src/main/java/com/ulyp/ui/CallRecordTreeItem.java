package com.ulyp.ui;

import com.ulyp.core.CallRecord;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class CallRecordTreeItem extends TreeItem<Node> {

    private final RenderSettings renderSettings;
    private final CallRecord node;
    private final int totalNodeCount;

    private boolean loaded = false;

    public CallRecordTreeItem(CallRecord node, RenderSettings renderSettings, int totalNodeCountInTree) {
        super(CallRecordTreeViewRenderer.render(node, renderSettings, totalNodeCountInTree));
        this.node = node;
        this.renderSettings = renderSettings;
        this.totalNodeCount = totalNodeCountInTree;
    }

    public void refresh() {
        setValue(CallRecordTreeViewRenderer.render(node, renderSettings, totalNodeCount));
        if (loaded) {
            getChildren().forEach(node -> ((CallRecordTreeItem) node).refresh());
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
        List<CallRecordTreeItem> children = new ArrayList<>();
        for (CallRecord child : node.getChildren()) {
            children.add(new CallRecordTreeItem(child, renderSettings, totalNodeCount));
        }

        super.getChildren().setAll(children);
    }

    public CallRecord getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "FxCallRecord{" +
                "node=" + node +
                '}';
    }
}
