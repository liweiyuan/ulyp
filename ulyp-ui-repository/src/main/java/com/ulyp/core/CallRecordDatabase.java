package com.ulyp.core;

import it.unimi.dsi.fastutil.longs.LongList;

import java.util.List;

public interface CallRecordDatabase {

    default CallRecord root() {
        return find(0);
    }

    CallRecord find(long id);

    void deleteSubtree(long id);

    List<CallRecord> getChildren(long id);

    LongList getChildrenIds(long id);

    long countAll();

    long getSubtreeCount(long id);

    void linkChild(long parentId, long childId);

    void close();
}
