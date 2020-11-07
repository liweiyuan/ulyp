package com.ulyp.core.impl;

import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordDatabase;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class HeapCallRecordDatabase implements CallRecordDatabase {

    private long idGenerator = 0;
    private final Map<Long, CallRecord> nodes = new HashMap<>();
    private final Map<Long, LongSet> children = new HashMap<>();

    @Override
    public synchronized CallRecord find(long id) {
        return nodes.get(id);
    }

    @Override
    public synchronized void deleteSubtree(long id) {
        for (CallRecord child : getChildren(id)) {
            deleteSubtree(child.getId());
        }
        nodes.remove(id);
    }

    @Override
    public synchronized List<CallRecord> getChildren(long id) {
        return children.computeIfAbsent(id, i -> new LongOpenHashSet()).stream().map(this::find).collect(Collectors.toList());
    }

    @Override
    public synchronized LongList getChildrenIds(long id) {
        return new LongArrayList(children.get(id));
    }

    @Override
    public synchronized void persist(CallRecord node) {
        if (node.getId() < 0) {
            long id = ++idGenerator;
            node.setId(id);
        }
        nodes.put(node.getId(), node);
    }

    @Override
    public synchronized void linkChild(long parentId, long childId) {
        children.computeIfAbsent(parentId, i -> new LongOpenHashSet()).add(childId);
    }
}
