package com.ulyp.core;

import it.unimi.dsi.fastutil.longs.LongList;

import java.util.List;

public interface CallGraphDatabase {

    CallTrace find(long id);

    void deleteSubtree(long id);

    List<CallTrace> getChildren(long id);

    void persist(CallTrace node);

    LongList searchSubtree(String text, CallTrace node);
}
