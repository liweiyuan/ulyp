package com.ulyp.ui;

import com.ulyp.core.CallTraceTree;
import com.ulyp.core.CallGraphDatabase;
import it.unimi.dsi.fastutil.longs.LongList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.time.Duration;

public class ProcessTab extends Tab {

    private final CallGraphDatabase database;
    private final TabPane callTreeTabs;

    private String lastAppliedSearchText = "";

    private long idGenerator = 0;

    ProcessTab(CallGraphDatabase database, TabPane processTabPane, String mainClassName) {
        super(mainClassName);

        this.database = database;

        TabPane tabPane = new TabPane();
        this.callTreeTabs = tabPane;
        setContent(tabPane);
        tabPane.prefHeightProperty().bind(processTabPane.heightProperty());
        tabPane.prefWidthProperty().bind(processTabPane.widthProperty());
    }

    public void add(CallTraceTree tree, RenderSettings renderSettings, Duration lifetime) {
        long id = idGenerator++;
        FxCallTraceTreeTab tab = new FxCallTraceTreeTab(callTreeTabs, tree, renderSettings, id, lifetime);
        callTreeTabs.getTabs().add(tab);
    }

    public FxCallTraceTreeTab getSelectedTreeTab() {
        return (FxCallTraceTreeTab) callTreeTabs.getSelectionModel().getSelectedItem();
    }

    public void addTree(CallTraceTree tree, RenderSettings renderSettings, Duration lifetime) {
        add(tree, renderSettings, lifetime);
    }

    private void clearSearch() {
        for (Tab tab : callTreeTabs.getTabs()) {
            FxCallTraceTreeTab fxCallTraceTreeTab = (FxCallTraceTreeTab) tab;
            fxCallTraceTreeTab.clearSearchMark();
        }
    }

    public void dispose() {
        for (Tab tab : callTreeTabs.getTabs()) {
            FxCallTraceTreeTab fxCallTraceTreeTab = (FxCallTraceTreeTab) tab;
            fxCallTraceTreeTab.dispose();
        }
    }

    public void applySearch(String searchText) {
        this.lastAppliedSearchText = searchText;

        if (searchText.trim().isEmpty()) {
            clearSearch();
            return;
        }

        for (Tab tab : callTreeTabs.getTabs()) {
            applySearchForTab((FxCallTraceTreeTab) tab, searchText);
        }
    }

    // TODO move into tab
    private void applySearchForTab(FxCallTraceTreeTab fxTab, String searchText) {
        FxCallTrace root = fxTab.getRoot();
        LongList result = database.searchSubtree(searchText, root.getNode());

        if (!result.isEmpty()) {
            fxTab.markHasSearchResults();
        }

        for (long id : result) {
            System.out.println(database.find(id));
        }
    }
}
