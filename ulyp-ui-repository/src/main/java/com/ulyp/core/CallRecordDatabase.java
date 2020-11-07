package com.ulyp.core;

import it.unimi.dsi.fastutil.longs.LongList;

import java.util.List;

public interface CallRecordDatabase {

    CallRecord find(long id);

    void deleteSubtree(long id);

    List<CallRecord> getChildren(long id);

    void persist(CallRecord node);

    void linkChild(long parentId, long childId);

    LongList searchSubtree(String text, CallRecord node);
}
