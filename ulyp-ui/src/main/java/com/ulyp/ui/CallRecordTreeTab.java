package com.ulyp.ui;

import com.ulyp.ui.code.SourceCode;
import com.ulyp.ui.code.SourceCodeFinder;
import com.ulyp.ui.code.SourceCodeView;
import javafx.scene.Node;
import javafx.scene.control.*;

import javax.annotation.Nullable;

public class CallRecordTreeTab extends Tab {

    private final CallRecordTree tree;
    private final TreeView<Node> view;

    @SuppressWarnings("unchecked")
    public CallRecordTreeTab(
            TabPane treesTabs,
            SourceCodeView sourceCodeView,
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
                    CallRecordTreeItem callRecord = (CallRecordTreeItem) newValue;
                    if (callRecord != null && callRecord.getNode() != null) {
                        SourceCode sourceCode = sourceCodeFinder.find(callRecord.getNode().getClassName());
                        sourceCodeView.setText(sourceCode, callRecord.getNode().getMethodName());
                    }
                }
        );

        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.prefHeightProperty().bind(treesTabs.heightProperty());
        scrollPane.prefWidthProperty().bind(treesTabs.widthProperty());

        setText(tree.getTabName());
        setContent(scrollPane);
        setOnClosed(ev -> dispose());
        setTooltip(tree.getTooltip());
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

    public void dispose() {
        this.tree.dispose();
    }
}
