package com.ulyp.ui;

import com.ulyp.ui.code.SourceCodeFinder;
import javafx.scene.Node;
import javafx.scene.control.*;

import javax.annotation.Nullable;

public class CallRecordTreeTab extends Tab {

    private final CallRecordTree tree;
    private final TreeView<Node> view;

    @SuppressWarnings("unchecked")
    public CallRecordTreeTab(
            TabPane treesTabs,
            CallRecordTree tree,
            RenderSettings renderSettings)
    {
        this.tree = tree;

        view = new TreeView<>(new CallRecordTreeItem(tree.getRoot(), renderSettings, tree.getRoot().getSubtreeNodeCount()));
        view.prefHeightProperty().bind(treesTabs.heightProperty());
        view.prefWidthProperty().bind(treesTabs.widthProperty());

        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(tree.getProcessInfo().getClasspathList());

        view.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    System.out.println("Selected Text : " + newValue);
                }
        );

        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.prefHeightProperty().bind(treesTabs.heightProperty());
        scrollPane.prefWidthProperty().bind(treesTabs.widthProperty());
        setText(tree.getRoot().getMethodName() + "(" + tree.getId() + ", life=" + tree.getLifetime().toMillis() + "ms, nodes=" + tree.getRoot().getSubtreeNodeCount() + ")");
        setContent(scrollPane);
        setOnClosed(ev -> dispose());

        setTooltip(new Tooltip("Thread: " + "12312312"));
    }

    public void markHasSearchResults() {
        setText("<>" + getText());
    }

    public void clearSearchMark() {
        setText(getText().replace("<>", ""));
    }

    @Nullable
    public CallRecordTreeItem getSelected() {
        return (CallRecordTreeItem) view.getSelectionModel().getSelectedItem();
    }

    public CallRecordTreeItem getRoot() {
        return (CallRecordTreeItem) view.getRoot();
    }

    public void dispose() {
        this.tree.dispose();
    }
}
