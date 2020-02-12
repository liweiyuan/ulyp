package com.ulyp.ui;

import com.ulyp.ui.util.MethodTraceTreeNode;
import com.ulyp.ui.util.MethodTraceTree;
import com.ulyp.ui.util.SearchIndex;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class MethodTreeList {

    private long counter = 0;

    private final TabPane stackTabs;
    private final Consumer<Event> onDestroy;

    private String textSearch = "";

    private final TabList visibleTabs;
    private final TabList invisibleTabs;

    public MethodTreeList(TabPane stackTabs, Consumer<Event> onDestroy) {
        this.visibleTabs = new TabList(stackTabs.getTabs());
        this.invisibleTabs = new TabList();

        this.stackTabs = stackTabs;
        this.onDestroy = onDestroy;
    }

    public void add(MethodTraceTree tree, long time) {
        long stamp = counter++;

        MethodTreeTab tab = new MethodTreeTab(from(tree, stamp, time), tree, tree.getSearchIndex(), stamp);

        if (tab.getSearchIndex().contains(textSearch)) {
            addToVisible(tab);
        } else {
            addToInvisible(tab);
        }
    }

    private void addToVisible(MethodTreeTab treeTab) {
        visibleTabs.add(treeTab);
        treeTab.getJavaFxTab().setOnClosed(
                event -> {
                    if (visibleTabs.isEmpty() && invisibleTabs.isEmpty()) {
                        onDestroy.accept(event);
                    }
                }
        );
    }

    private void addToInvisible(MethodTreeTab treeTab) {
        invisibleTabs.add(treeTab);
        treeTab.getJavaFxTab().setOnClosed(
                event -> {
                    if (visibleTabs.isEmpty() && invisibleTabs.isEmpty()) {
                        onDestroy.accept(event);
                    }
                }
        );
    }

    private Tab from(MethodTraceTree tree, long stamp, long time) {
        TreeView<TextFlow> view = new TreeView<>(fromNode(tree.getRoot()));
        view.prefHeightProperty().bind(stackTabs.heightProperty());
        view.prefWidthProperty().bind(stackTabs.widthProperty());
        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.prefHeightProperty().bind(stackTabs.heightProperty());
        scrollPane.prefWidthProperty().bind(stackTabs.widthProperty());
        return new Tab(
                tree.getRoot().getMethodInfo().getMethodName() + "(" + stamp + ", " + time + "ms)",
                scrollPane
        );
    }

    private TreeItem<TextFlow> fromNode(MethodTraceTreeNode node) {
        TreeItem<TextFlow> item = new TreeItem<>(node.toTextFlow());

        for (MethodTraceTreeNode child : node.getChildren()) {
            item.getChildren().add(fromNode(child));
        }
        return item;
    }

    public void applySearch(String strToSearch) {
        this.textSearch = strToSearch;

        List<MethodTreeTab> tabs = new ArrayList<>(visibleTabs.getTabs());
        tabs.addAll(invisibleTabs.getTabs());
        tabs.sort(Comparator.comparing(MethodTreeTab::getOrderStamp));

        visibleTabs.clear();
        invisibleTabs.clear();

        for (int i = 0; i < tabs.size(); i++) {
            SearchIndex index = tabs.get(i).getSearchIndex();
            if (index.contains(strToSearch)) {
                addToVisible(tabs.get(i));
            } else {
                addToInvisible(tabs.get(i));
            }
        }
    }

    public void selectNextLeftTab() {
        List<MethodTreeTab> tabs = visibleTabs.getTabs();
        for (int i = 1; i < tabs.size(); i++) {
            if (tabs.get(i).getJavaFxTab().isSelected()) {
                stackTabs.getSelectionModel().select(i - 1);
                return;
            }
        }
    }

    public void selectNextRightTab() {
        List<MethodTreeTab> tabs = visibleTabs.getTabs();
        for (int i = 0; i < tabs.size() - 1; i++) {
            if (tabs.get(i).getJavaFxTab().isSelected()) {
                stackTabs.getSelectionModel().select(i + 1);
                return;
            }
        }
    }

    public List<MethodTreeTab> methodTrees() {
        List<MethodTreeTab> l = new ArrayList<>();
        l.addAll(visibleTabs.getTabs());
        l.addAll(invisibleTabs.getTabs());
        return l;
    }
}
