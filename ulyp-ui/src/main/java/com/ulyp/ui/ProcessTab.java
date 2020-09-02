package com.ulyp.ui;

import com.ulyp.ui.code.SourceCodeView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ProcessTab extends Tab {

    private final String mainClassName;
    private final TabPane callTreeTabs;
    private final SourceCodeView sourceCodeView;

    ProcessTab(TabPane processTabPane, SourceCodeView sourceCodeView, String mainClassName) {
        super(mainClassName);

        this.mainClassName = mainClassName;

        this.sourceCodeView = sourceCodeView;

        TabPane tabPane = new TabPane();
        this.callTreeTabs = tabPane;
        setContent(tabPane);

        // TODO get rid of that?
        tabPane.prefHeightProperty().bind(processTabPane.heightProperty());
        tabPane.prefWidthProperty().bind(processTabPane.widthProperty());
    }

    public String getMainClassName() {
        return mainClassName;
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
}
