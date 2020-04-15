package com.ulyp.storage.inmem;

import com.ulyp.storage.MethodTraceTreeNode;
import com.ulyp.storage.ObjectValue;
import com.ulyp.storage.Storage;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.apache.commons.lang3.StringUtils;

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

    @Override
    public LongList searchSubtree(String text, MethodTraceTreeNode node) {
        LongList result = new LongArrayList();
        searchSubtreeRecursive(result, text, node);
        return result;
    }

    public void searchSubtreeRecursive(LongList resultList, String text, MethodTraceTreeNode node) {
        if (matches(text, node)) {
            resultList.add(node.getId());
        }
        for (MethodTraceTreeNode child : getChildren(node.getId())) {
            searchSubtreeRecursive(resultList, text, child);
        }
    }

    // TODO move to node
    private boolean matches(String text, MethodTraceTreeNode node) {
        if (StringUtils.containsIgnoreCase(node.getReturnValue().getPrintedText(), text)) {
            return true;
        }
        for (ObjectValue arg: node.getArgs()) {
            if (StringUtils.containsIgnoreCase(arg.getPrintedText(), text)) {
                return true;
            }
        }
        return StringUtils.containsIgnoreCase(node.getMethodName(), text) || StringUtils.containsIgnoreCase(node.getClassName(), text);
    }
}
