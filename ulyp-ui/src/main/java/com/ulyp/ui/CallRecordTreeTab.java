package com.ulyp.ui;

import com.ulyp.ui.code.SourceCode;
import com.ulyp.ui.code.SourceCodeFinder;
import com.ulyp.ui.code.SourceCodeView;
import com.ulyp.ui.font.FontSizeChanger;
import com.ulyp.ui.util.ResizeEvent;
import com.ulyp.ui.util.ResizeEventSupportingScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

@Component
@Scope(value = "prototype")
public class CallRecordTreeTab extends Tab {

    private final Region parent;
    private final CallRecordTree tree;

    private TreeView<CallTreeNodeContent> treeView;

    @Autowired
    private SourceCodeView sourceCodeView;
    @Autowired
    private RenderSettings renderSettings;
    @Autowired
    private FontSizeChanger fontSizeChanger;

    @SuppressWarnings("unchecked")
    public CallRecordTreeTab(Region parent, CallRecordTree tree) {
        this.tree = tree;
        this.parent = parent;
    }

    @PostConstruct
    public void init() {
        treeView = new TreeView<>(new CallRecordTreeNode(tree.getRoot(), renderSettings, tree.getRoot().getSubtreeNodeCount()));
        treeView.prefHeightProperty().bind(parent.heightProperty());
        treeView.prefWidthProperty().bind(parent.widthProperty());

        SourceCodeFinder sourceCodeFinder = new SourceCodeFinder(tree.getProcessInfo().getClasspathList());

        treeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    CallRecordTreeNode callRecord = (CallRecordTreeNode) newValue;
                    if (callRecord != null && callRecord.getCallRecord() != null) {
                        SourceCode sourceCode = sourceCodeFinder.find(callRecord.getCallRecord().getClassName());
                        sourceCodeView.setText(sourceCode, callRecord.getCallRecord().getMethodName());
                    }
                }
        );

        ResizeEventSupportingScrollPane scrollPane = new ResizeEventSupportingScrollPane(treeView);

        scrollPane.addListener(
                resizeEvent -> {
                    if (resizeEvent == ResizeEvent.UP) {
                        fontSizeChanger.upscale(parent.getScene());
                    } else {
                        fontSizeChanger.downscale(parent.getScene());
                    }
                }
        );

        setText(tree.getTabName());
        setContent(scrollPane);
        setOnClosed(ev -> dispose());
        setTooltip(tree.getTooltip());
    }

    @Nullable
    public CallRecordTreeNode getSelected() {
        return (CallRecordTreeNode) treeView.getSelectionModel().getSelectedItem();
    }

    public void dispose() {
        this.tree.dispose();
    }
}
