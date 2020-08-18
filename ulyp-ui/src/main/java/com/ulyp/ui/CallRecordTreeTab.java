package com.ulyp.ui;

import com.ulyp.core.CallRecordDatabase;
import com.ulyp.core.CallRecordTree;
import com.ulyp.transport.ProcessInfo;
import com.ulyp.ui.code.SourceCodeFinder;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;

import javax.annotation.Nullable;
import java.time.Duration;

public class CallRecordTreeTab extends Tab {

    private final CallRecordTree tree;
    private final CallRecordDatabase database;
    private final TreeView<Node> view;

    @SuppressWarnings("unchecked")
    public CallRecordTreeTab(
            CallRecordDatabase database,
            TabPane treesTabs,
            ProcessInfo processInfo,
            CallRecordTree tree,
            RenderSettings renderSettings,
            long id,
            Duration lifetime)
    {
        this.tree = tree;
        this.database = database;

        view = new TreeView<>(new FxCallRecord(tree.getRoot(), renderSettings, tree.getRoot().getSubtreeNodeCount()));
        view.prefHeightProperty().bind(treesTabs.heightProperty());
        view.prefWidthProperty().bind(treesTabs.widthProperty());

        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(processInfo.getClasspathList());

        view.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    System.out.println("Selected Text : " + newValue);
                }
        );

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
        // TODO return
        database.deleteSubtree(tree.getRoot().getId());
//        tree.getRoot().delete();
    }
}
