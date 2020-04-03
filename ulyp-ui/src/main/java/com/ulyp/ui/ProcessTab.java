package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProcessTab extends Tab {

    private final TabPane treesTabs;
    private final Consumer<Event> onDestroy;
    private final List<MethodTraceTreeTab> invisibleTabs;

    private String textSearch = "";
    private long idGenerator = 0;

    ProcessTab(TabPane processTabPane, String mainClassName, Consumer<Event> onClose) {
        super(mainClassName);

        TabPane tabPane = new TabPane();
        this.invisibleTabs = new ArrayList<>();
        this.treesTabs = tabPane;
        this.onDestroy = onClose;
        setContent(tabPane);
        tabPane.prefHeightProperty().bind(processTabPane.heightProperty());
        tabPane.prefWidthProperty().bind(processTabPane.widthProperty());
    }

    public void add(MethodTraceTreeNode tree, RenderSettings renderSettings, Duration lifetime) {
        long id = idGenerator++;

        MethodTraceTreeTab tab = new MethodTraceTreeTab(treesTabs, tree, renderSettings, id, lifetime);

//        if (tab.getSearchIndex().contains(textSearch)) {
        addToVisible(tab);
//        } else {
//            addToInvisible(tab);
//        }
    }

    public MethodTraceTreeTab getSelectedTreeTab() {
        return (MethodTraceTreeTab) treesTabs.getSelectionModel().getSelectedItem();
    }

    public void addTree(MethodTraceTreeNode tree, RenderSettings renderSettings, Duration lifetime) {
        add(tree, renderSettings, lifetime);
    }

    private void addToVisible(MethodTraceTreeTab treeTab) {
        treesTabs.getTabs().add(treeTab);
        treeTab.setOnClosed(
                event -> {
                    if (treesTabs.getTabs().isEmpty() && invisibleTabs.isEmpty()) {
                        onDestroy.accept(event);
                    }
                }
        );
    }

    private void addToInvisible(MethodTraceTreeTab treeTab) {
        invisibleTabs.add(treeTab);
        treeTab.setOnClosed(
                event -> {
                    if (treesTabs.getTabs().isEmpty() && invisibleTabs.isEmpty()) {
                        onDestroy.accept(event);
                    }
                }
        );
    }

    public void applySearch(String strToSearch) {
        this.textSearch = strToSearch;

//        List<MethodTraceTreeTab> tabs = new ArrayList<>(visibleTabs.getTabs());
//        tabs.addAll(invisibleTabs.getTabs());
//        tabs.sort(Comparator.comparing(MethodTraceTreeTab::getOrderStamp));
//
//        visibleTabs.clear();
//        invisibleTabs.clear();
//
//        for (int i = 0; i < tabs.size(); i++) {
//            SearchIndex index = tabs.get(i).getSearchIndex();
//            if (index.contains(strToSearch)) {
//                addToVisible(tabs.get(i));
//            } else {
//                addToInvisible(tabs.get(i));
//            }
//        }
    }
}
