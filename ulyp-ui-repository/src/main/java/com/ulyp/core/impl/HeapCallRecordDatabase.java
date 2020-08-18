package com.ulyp.core.impl;

import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordDatabase;
import com.ulyp.core.ObjectValue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class HeapCallRecordDatabase implements CallRecordDatabase {

    private final AtomicLong idGenerator = new AtomicLong();
    private final Map<Long, CallRecord> nodes = new ConcurrentHashMap<>();

    @Override
    public CallRecord find(long id) {
        return nodes.get(id);
    }

    @Override
    public void deleteSubtree(long id) {
        CallRecord callRecord = nodes.get(id);
        for (CallRecord child : callRecord.getChildren()) {
            deleteSubtree(child.getId());
        }
        nodes.remove(id);
    }

    @Override
    public List<CallRecord> getChildren(long id) {
        CallRecord node = find(id);
        if (node != null) {
            return node.getChildren();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void persist(CallRecord node) {
        long id = idGenerator.incrementAndGet();
        node.setId(id);
        nodes.put(id, node);
    }

    @Override
    public LongList searchSubtree(String text, CallRecord node) {
        LongList result = new LongArrayList();
        searchSubtreeRecursive(result, text, node);
        return result;
    }

    public void searchSubtreeRecursive(LongList resultList, String text, CallRecord node) {
        if (matches(text, node)) {
            resultList.add(node.getId());
        }
        for (CallRecord child : getChildren(node.getId())) {
            searchSubtreeRecursive(resultList, text, child);
        }
    }

    // TODO move to node
    private boolean matches(String text, CallRecord node) {
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
