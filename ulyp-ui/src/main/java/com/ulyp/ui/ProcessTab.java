package com.ulyp.ui;

import com.ulyp.core.CallRecordTree;
import com.ulyp.core.CallRecordDatabase;
import com.ulyp.transport.ProcessInfo;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.time.Duration;

public class ProcessTab extends Tab {

    private final CallRecordDatabase database;
    private final TabPane callTreeTabs;

    private long idGenerator = 0;

    ProcessTab(CallRecordDatabase database, TabPane processTabPane, String mainClassName) {
        super(mainClassName);

        this.database = database;

        TabPane tabPane = new TabPane();
        this.callTreeTabs = tabPane;
        setContent(tabPane);
        tabPane.prefHeightProperty().bind(processTabPane.heightProperty());
        tabPane.prefWidthProperty().bind(processTabPane.widthProperty());
    }

    public void add(CallRecordTree tree, ProcessInfo processInfo, RenderSettings renderSettings, Duration lifetime) {
        long id = idGenerator++;
        CallRecordTreeTab tab = new CallRecordTreeTab(database, callTreeTabs, processInfo, tree, renderSettings, id, lifetime);
        callTreeTabs.getTabs().add(tab);
    }

    public CallRecordTreeTab getSelectedTreeTab() {
        return (CallRecordTreeTab) callTreeTabs.getSelectionModel().getSelectedItem();
    }

    public void addTree(CallRecordTree tree, ProcessInfo processInfo, RenderSettings renderSettings, Duration lifetime) {
        add(tree, processInfo, renderSettings, lifetime);
    }

    private void clearSearch() {
        for (Tab tab : callTreeTabs.getTabs()) {
            CallRecordTreeTab callRecordTreeTab = (CallRecordTreeTab) tab;
            callRecordTreeTab.clearSearchMark();
        }
    }

    public void dispose() {
        for (Tab tab : callTreeTabs.getTabs()) {
            CallRecordTreeTab fxCallRecordTreeTab = (CallRecordTreeTab) tab;
            fxCallRecordTreeTab.dispose();
        }
    }

    public void applySearch(String searchText) {
//        this.lastAppliedSearchText = searchText;
//
//        if (searchText.trim().isEmpty()) {
//            clearSearch();
//            return;
//        }
//
//        for (Tab tab : callTreeTabs.getTabs()) {
//            applySearchForTab((FxCallRecordTreeTab) tab, searchText);
//        }
    }

//    // TODO move into tab
//    private void applySearchForTab(FxCallRecordTreeTab fxTab, String searchText) {
//        FxCallRecord root = fxTab.getRoot();
//        LongList result = database.searchSubtree(searchText, root.getNode());
//
//        if (!result.isEmpty()) {
//            fxTab.markHasSearchResults();
//        }
//
//        for (long id : result) {
//            System.out.println(database.find(id));
//        }
//    }
}
