package com.ulyp.ui;

import com.ulyp.ui.util.FxThreadExecutor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(scopeName = "prototype")
public class ProcessTab extends Tab {

    private final String mainClassName;
    private TabPane callTreeTabs;
    @Autowired
    private ApplicationContext applicationContext;

    private final Map<Long, CallRecordTreeTab> tabsByRecordingId = new ConcurrentHashMap<>();

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

    public CallRecordTreeTab getOrCreateRecordingTab(final long recordingId) {
        return FxThreadExecutor.execute(
                () -> tabsByRecordingId.computeIfAbsent(recordingId, rId -> {
                    CallRecordTreeTab tab = applicationContext.getBean(CallRecordTreeTab.class, callTreeTabs);
                    callTreeTabs.getTabs().add(tab);
                    tab.setOnClosed(ev -> {
                        this.tabsByRecordingId.remove(recordingId);
                    });
                    return tab;
                })
        );
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
