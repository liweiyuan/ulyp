package com.ulyp.core.heap;

import com.ulyp.core.CallTrace;
import com.ulyp.core.CallGraphDatabase;
import com.ulyp.core.ObjectValue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class HeapCallGraphDatabase implements CallGraphDatabase {

    private final AtomicLong idGenerator = new AtomicLong();
    private final Map<Long, CallTrace> nodes = new ConcurrentHashMap<>();

    @Override
    public CallTrace find(long id) {
        return nodes.get(id);
    }

    @Override
    public void deleteSubtree(long id) {
        CallTrace callTrace = nodes.get(id);
        for (CallTrace child : callTrace.getChildren()) {
            deleteSubtree(child.getId());
        }
        nodes.remove(id);
    }

    @Override
    public List<CallTrace> getChildren(long id) {
        CallTrace node = find(id);
        if (node != null) {
            return node.getChildren();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void persist(CallTrace node) {
        long id = idGenerator.incrementAndGet();
        node.setId(id);
        node.setDatabase(this);
        nodes.put(id, node);
    }

    @Override
    public LongList searchSubtree(String text, CallTrace node) {
        LongList result = new LongArrayList();
        searchSubtreeRecursive(result, text, node);
        return result;
    }

    public void searchSubtreeRecursive(LongList resultList, String text, CallTrace node) {
        if (matches(text, node)) {
            resultList.add(node.getId());
        }
        for (CallTrace child : getChildren(node.getId())) {
            searchSubtreeRecursive(resultList, text, child);
        }
    }

    // TODO move to node
    private boolean matches(String text, CallTrace node) {
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