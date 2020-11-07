package com.ulyp.ui;

import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordDatabase;
import it.unimi.dsi.fastutil.longs.LongList;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class CallRecordTreeNode extends TreeItem<CallTreeNodeContent> {

    private final RenderSettings renderSettings;
    private final CallRecordDatabase database;
    private final long callRecordId;
    private final int totalNodeCount;

    private boolean loaded = false;
    private int loadedChildren = 0;

    public CallRecordTreeNode(CallRecordDatabase database, long callRecordId, RenderSettings renderSettings, int totalNodeCountInTree) {
        super(new CallTreeNodeContent(database.find(callRecordId), renderSettings, totalNodeCountInTree));
        this.database = database;
        this.callRecordId = callRecordId;
        this.renderSettings = renderSettings;
        this.totalNodeCount = totalNodeCountInTree;
    }

    public void refresh() {
        setValue(new CallTreeNodeContent(database.find(callRecordId), renderSettings, totalNodeCount));
        if (loaded) {
//            LongList childrenIds = database.getChildrenIds(callRecordId);

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

    public boolean isLoaded() {
        return loaded;
    }

    private void loadChildren() {
        List<CallRecordTreeNode> children = new ArrayList<>();
        for (CallRecord child : database.getChildren(callRecordId)) {
            // TODO optimize
            children.add(new CallRecordTreeNode(database, child.getId(), renderSettings, totalNodeCount));
        }

        super.getChildren().setAll(children);
        loaded = true;
    }

    public CallRecord getCallRecord() {
        return database.find(callRecordId);
    }

    @Override
    public String toString() {
        return "FxCallRecord{" +
                "node=" + getCallRecord() +
                '}';
    }
}
