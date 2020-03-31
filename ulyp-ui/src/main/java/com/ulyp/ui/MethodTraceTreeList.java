package com.ulyp.ui;

import com.ulyp.storage.MethodTraceTreeNode;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class MethodTraceTreeList {

    private final TabPane stackTabs;
    private final Consumer<Event> onDestroy;
    private final TabList visibleTabs;
    private final TabList invisibleTabs;

    private String textSearch = "";
    private long idGenerator = 0;

    public MethodTraceTreeList(TabPane stackTabs, Consumer<Event> onDestroy) {
        this.visibleTabs = new TabList(stackTabs.getTabs());
        this.invisibleTabs = new TabList();

        this.stackTabs = stackTabs;
        this.onDestroy = onDestroy;
    }

    public void add(MethodTraceTreeNode tree, RenderSettings renderSettings, Duration lifetime) {
        long id = idGenerator++;

        MethodTraceTreeTab tab = new MethodTraceTreeTab(tabFrom(tree, renderSettings, id, lifetime), id);

//        if (tab.getSearchIndex().contains(textSearch)) {
        addToVisible(tab);
//        } else {
//            addToInvisible(tab);
//        }
    }

    private void addToVisible(MethodTraceTreeTab treeTab) {
        visibleTabs.add(treeTab);
        treeTab.getJavaFxTab().setOnClosed(
                event -> {
                    if (visibleTabs.isEmpty() && invisibleTabs.isEmpty()) {
                        onDestroy.accept(event);
                    }
                }
        );
    }

    private void addToInvisible(MethodTraceTreeTab treeTab) {
        invisibleTabs.add(treeTab);
        treeTab.getJavaFxTab().setOnClosed(
                event -> {
                    if (visibleTabs.isEmpty() && invisibleTabs.isEmpty()) {
                        onDestroy.accept(event);
                    }
                }
        );
    }

    private Tab tabFrom(MethodTraceTreeNode node, RenderSettings renderSettings, long stamp, Duration lifetime) {
        TreeView<Node> view = new TreeView<>(new MethodTraceTreeFxItem(node, renderSettings, node.getSubtreeNodeCount()));
        view.prefHeightProperty().bind(stackTabs.heightProperty());
        view.prefWidthProperty().bind(stackTabs.widthProperty());
        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.prefHeightProperty().bind(stackTabs.heightProperty());
        scrollPane.prefWidthProperty().bind(stackTabs.widthProperty());
        return new Tab(
                node.getMethodName() + "(" + stamp + ", life=" + lifetime.toMillis() + "ms, nodes=" + node.getSubtreeNodeCount() + ")",
                scrollPane
        );
    }

    public void applySearch(String strToSearch) {
        this.textSearch = strToSearch;

        List<MethodTraceTreeTab> tabs = new ArrayList<>(visibleTabs.getTabs());
        tabs.addAll(invisibleTabs.getTabs());
        tabs.sort(Comparator.comparing(MethodTraceTreeTab::getOrderStamp));

        visibleTabs.clear();
        invisibleTabs.clear();

        for (int i = 0; i < tabs.size(); i++) {
//            SearchIndex index = tabs.get(i).getSearchIndex();
//            if (index.contains(strToSearch)) {
                addToVisible(tabs.get(i));
//            } else {
//                addToInvisible(tabs.get(i));
//            }
        }
    }

    public void selectNextLeftTab() {
        List<MethodTraceTreeTab> tabs = visibleTabs.getTabs();
        for (int i = 1; i < tabs.size(); i++) {
            if (tabs.get(i).getJavaFxTab().isSelected()) {
                stackTabs.getSelectionModel().select(i - 1);
                return;
            }
        }
    }

    public void selectNextRightTab() {
        List<MethodTraceTreeTab> tabs = visibleTabs.getTabs();
        for (int i = 0; i < tabs.size() - 1; i++) {
            if (tabs.get(i).getJavaFxTab().isSelected()) {
                stackTabs.getSelectionModel().select(i + 1);
                return;
            }
        }
    }
}
