package com.ulyp.ui;

import com.ulyp.core.CallRecordTree;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;

import javax.annotation.Nullable;
import java.time.Duration;

public class FxCallRecordTreeTab extends Tab {

    private final CallRecordTree tree;
    private final TreeView<Node> view;

    @SuppressWarnings("unchecked")
    public FxCallRecordTreeTab(TabPane treesTabs, CallRecordTree tree, RenderSettings renderSettings, long id, Duration lifetime) {
        this.tree = tree;
        view = new TreeView<>(new FxCallRecord(tree.getRoot(), renderSettings, tree.getRoot().getSubtreeNodeCount()));
        view.prefHeightProperty().bind(treesTabs.heightProperty());
        view.prefWidthProperty().bind(treesTabs.widthProperty());
        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.prefHeightProperty().bind(treesTabs.heightProperty());
        scrollPane.prefWidthProperty().bind(treesTabs.widthProperty());
        setText(tree.getRoot().getMethodName() + "(" + id + ", life=" + lifetime.toMillis() + "ms, nodes=" + tree.getRoot().getSubtreeNodeCount() + ")");
        setContent(scrollPane);
        setOnClosed(ev -> dispose());
    }

    public void markHasSearchResults() {
        setText("<>" + getText());
    }

    public void clearSearchMark() {
        setText(getText().replace("<>", ""));
    }

    @Nullable
    public FxCallRecord getSelected() {
        return (FxCallRecord) view.getSelectionModel().getSelectedItem();
    }

    public FxCallRecord getRoot() {
        return (FxCallRecord) view.getRoot();
    }

    public void dispose() {
        tree.getRoot().delete();
    }
}
