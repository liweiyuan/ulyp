package com.ulyp.storage.inmem;

import com.ulyp.storage.MethodTraceTreeNode;
import com.ulyp.storage.Storage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryStorage implements Storage {

    private final AtomicLong idGenerator = new AtomicLong();
    private final Map<Long, MethodTraceTreeNode> nodes = new ConcurrentHashMap<>();

    @Override
    public MethodTraceTreeNode find(long id) {
        return nodes.get(id);
    }

    @Override
    public List<MethodTraceTreeNode> getChildren(long id) {
        MethodTraceTreeNode node = find(id);
        if (node != null) {
            return node.getChildren();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void persist(MethodTraceTreeNode node) {
        long id = idGenerator.incrementAndGet();
        node.setId(id);
        nodes.put(id, node);
    }
}
