package com.ulyp.storage;

import it.unimi.dsi.fastutil.longs.LongList;

import java.util.List;

public interface Storage {

    MethodTraceTreeNode find(long id);

    List<MethodTraceTreeNode> getChildren(long id);

    void persist(MethodTraceTreeNode node);

    LongList searchSubtree(String text, MethodTraceTreeNode node);
}
