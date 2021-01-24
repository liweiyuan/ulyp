package com.ulyp.ui;

import com.ulyp.core.CallRecord;
import com.ulyp.ui.util.FxThreadExecutor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class ProcessTabPane extends TabPane {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private RenderSettings renderSettings;

    public ProcessTabPane() {
        setOnKeyPressed(this::keyPressed);
        setOnKeyReleased(this::keyReleased);
    }

    public void clear() {
        for (Tab tab : getTabs()) {
            ProcessTab processTab = (ProcessTab) tab;
            processTab.dispose();
        }
        getTabs().clear();
    }

    public ProcessTab getSelectedTab() {
        return (ProcessTab) getSelectionModel().getSelectedItem();
    }

    @NotNull
    public ProcessTab getOrCreateProcessTab(String mainClassName) {
        return FxThreadExecutor.execute(
                () -> {
                    Optional<Tab> processTab = getTabs()
                            .stream()
                            .filter(tab -> mainClassName.equals(((ProcessTab) tab).getMainClassName()))
                            .findFirst();

                    if (processTab.isPresent()) {
                        return (ProcessTab) processTab.get();
                    } else {
                        ProcessTab tab = context.getBean(ProcessTab.class, mainClassName);
                        getTabs().add(tab);
                        return tab;
                    }
                }
        );
    }

    public void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            CallRecordTreeNode selected = getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowTypes(true);
                selected.refresh();
            }
        } else {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                // COPY currently selected
                ProcessTab selectedTab = getSelectedTab();
                if (selectedTab != null) {
                    CallRecordTreeNode selectedCallRecord = getSelectedTab().getSelectedTreeTab().getSelected();
                    if (selectedCallRecord != null) {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        CallRecord callRecord = selectedCallRecord.getCallRecord();
                        content.putString(callRecord.getClassName() + "." + callRecord.getMethodName());
                        clipboard.setContent(content);
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.SHIFT) {
            CallRecordTreeNode selected = getSelectedTab().getSelectedTreeTab().getSelected();
            if (selected != null) {
                renderSettings.setShowTypes(false);
                selected.refresh();
            }
        }
    }
}
