package com.ulyp.ui;

import com.ulyp.ui.code.SourceCodeView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ProcessTab extends Tab {

    private final TabPane callTreeTabs;
    private final SourceCodeView sourceCodeView;

    ProcessTab(TabPane processTabPane, SourceCodeView sourceCodeView, String mainClassName) {
        super(mainClassName);

        this.sourceCodeView = sourceCodeView;

        TabPane tabPane = new TabPane();
        this.callTreeTabs = tabPane;
        setContent(tabPane);
        tabPane.prefHeightProperty().bind(processTabPane.heightProperty());
        tabPane.prefWidthProperty().bind(processTabPane.widthProperty());
    }

    public void add(CallRecordTree tree, RenderSettings renderSettings) {
        CallRecordTreeTab tab = new CallRecordTreeTab(callTreeTabs, sourceCodeView, tree, renderSettings);
        callTreeTabs.getTabs().add(tab);
    }

    public CallRecordTreeTab getSelectedTreeTab() {
        return (CallRecordTreeTab) callTreeTabs.getSelectionModel().getSelectedItem();
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
