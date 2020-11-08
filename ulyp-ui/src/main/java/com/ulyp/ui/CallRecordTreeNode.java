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

    private boolean loaded = false;

    public CallRecordTreeNode(CallRecordDatabase database, long callRecordId, RenderSettings renderSettings) {
        super(new CallTreeNodeContent(database.find(callRecordId), renderSettings, database.countAll()));
        this.database = database;
        this.callRecordId = callRecordId;
        this.renderSettings = renderSettings;
    }

    public void refresh() {
        setValue(new CallTreeNodeContent(database.find(callRecordId), renderSettings, database.countAll()));
        if (loaded) {
            LongList newChildren = database.getChildrenIds(callRecordId);
            int currentLoadedChildrenCount = getChildren().size();

            if (newChildren.size() > currentLoadedChildrenCount) {
                for (int i = currentLoadedChildrenCount; i < newChildren.size(); i++) {
                    getChildren().add(new CallRecordTreeNode(database, newChildren.getLong(i), renderSettings));
                }
            }

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
        List<CallRecordTreeNode> children = new ArrayList<>();

        LongList childrenIds = database.getChildrenIds(callRecordId);
        for (int i = 0; i < childrenIds.size(); i++) {
            children.add(new CallRecordTreeNode(database, childrenIds.getLong(i), renderSettings));
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
