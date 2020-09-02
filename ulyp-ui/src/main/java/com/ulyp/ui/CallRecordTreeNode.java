package com.ulyp.ui;

import com.ulyp.core.CallRecord;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class CallRecordTreeNode extends TreeItem<CallTreeNodeContent> {

    private final RenderSettings renderSettings;
    private final CallRecord callRecord;
    private final int totalNodeCount;

    private boolean loaded = false;

    public CallRecordTreeNode(CallRecord callRecord, RenderSettings renderSettings, int totalNodeCountInTree) {
        super(new CallTreeNodeContent(callRecord, renderSettings, totalNodeCountInTree));
        this.callRecord = callRecord;
        this.renderSettings = renderSettings;
        this.totalNodeCount = totalNodeCountInTree;
    }

    public void refresh() {
        setValue(new CallTreeNodeContent(callRecord, renderSettings, totalNodeCount));
        if (loaded) {
            getChildren().forEach(node -> ((CallRecordTreeNode) node).refresh());
        }
    }

    @Override
    public ObservableList<TreeItem<CallTreeNodeContent>> getChildren() {
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
        List<CallRecordTreeNode> children = new ArrayList<>();
        for (CallRecord child : callRecord.getChildren()) {
            children.add(new CallRecordTreeNode(child, renderSettings, totalNodeCount));
        }

        super.getChildren().setAll(children);
    }

    public CallRecord getCallRecord() {
        return callRecord;
    }

    @Override
    public String toString() {
        return "FxCallRecord{" +
                "node=" + callRecord +
                '}';
    }
}
