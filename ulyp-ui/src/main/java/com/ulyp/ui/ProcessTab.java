package com.ulyp.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope(scopeName = "prototype")
public class ProcessTab extends Tab {

    private final String mainClassName;
    private TabPane callTreeTabs;
    @Autowired
    private ApplicationContext applicationContext;

    ProcessTab(String mainClassName) {
        super(mainClassName);

        this.mainClassName = mainClassName;
    }

    @PostConstruct
    private void init() {
        TabPane tabPane = new TabPane();
        this.callTreeTabs = tabPane;
        setContent(tabPane);
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void add(CallRecordTree tree) {
        CallRecordTreeTab tab = applicationContext.getBean(CallRecordTreeTab.class, callTreeTabs, tree);

        callTreeTabs.getTabs().add(tab);
    }

    public CallRecordTreeTab getSelectedTreeTab() {
        return (CallRecordTreeTab) callTreeTabs.getSelectionModel().getSelectedItem();
    }

    public void dispose() {
        for (Tab tab : callTreeTabs.getTabs()) {
            CallRecordTreeTab fxCallRecordTreeTab = (CallRecordTreeTab) tab;
            fxCallRecordTreeTab.dispose();
        }
    }
}
