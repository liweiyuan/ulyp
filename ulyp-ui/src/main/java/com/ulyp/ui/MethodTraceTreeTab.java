package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;

import javax.annotation.Nullable;
import java.time.Duration;

public class MethodTraceTreeTab extends Tab {

    private final TreeView<Node> view;

    @SuppressWarnings("unchecked")
    public MethodTraceTreeTab(TabPane treesTabs, MethodTraceTreeNode node, RenderSettings renderSettings, long id, Duration lifetime) {
        view = new TreeView<>(new MethodTraceTreeFxItem(node, renderSettings, node.getSubtreeNodeCount()));
        view.prefHeightProperty().bind(treesTabs.heightProperty());
        view.prefWidthProperty().bind(treesTabs.widthProperty());
        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.prefHeightProperty().bind(treesTabs.heightProperty());
        scrollPane.prefWidthProperty().bind(treesTabs.widthProperty());
        setText(node.getMethodName() + "(" + id + ", life=" + lifetime.toMillis() + "ms, nodes=" + node.getSubtreeNodeCount() + ")");
        setContent(scrollPane);
    }

    @Nullable
    public MethodTraceTreeFxItem getSelected() {
        return (MethodTraceTreeFxItem) view.getSelectionModel().getSelectedItem();
    }
}
