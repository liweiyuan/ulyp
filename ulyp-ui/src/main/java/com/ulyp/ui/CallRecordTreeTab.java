package com.ulyp.ui;

import com.ulyp.ui.code.SourceCode;
import com.ulyp.ui.code.SourceCodeFinder;
import com.ulyp.ui.code.SourceCodeView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;

import javax.annotation.Nullable;

public class CallRecordTreeTab extends Tab {

    private final CallRecordTree tree;
    private final TreeView<CallTreeNodeContent> view;

    @SuppressWarnings("unchecked")
    public CallRecordTreeTab(
            TabPane treesTabs,
            SourceCodeView sourceCodeView,
            CallRecordTree tree,
            RenderSettings renderSettings)
    {
        this.tree = tree;

        view = new TreeView<>(new CallRecordTreeNode(tree.getRoot(), renderSettings, tree.getRoot().getSubtreeNodeCount()));
        view.prefHeightProperty().bind(treesTabs.heightProperty());
        view.prefWidthProperty().bind(treesTabs.widthProperty());

        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(tree.getProcessInfo().getClasspathList());

        view.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    CallRecordTreeNode callRecord = (CallRecordTreeNode) newValue;
                    if (callRecord != null && callRecord.getCallRecord() != null) {
                        SourceCode sourceCode = sourceCodeFinder.find(callRecord.getCallRecord().getClassName());
                        sourceCodeView.setText(sourceCode, callRecord.getCallRecord().getMethodName());
                    }
                }
        );

        setText(tree.getTabName());
        setContent(new ScrollPane(view));
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
    public CallRecordTreeNode getSelected() {
        return (CallRecordTreeNode) view.getSelectionModel().getSelectedItem();
    }

    public void dispose() {
        this.tree.dispose();
    }
}
