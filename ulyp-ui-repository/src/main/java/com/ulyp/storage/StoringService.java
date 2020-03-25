package com.ulyp.storage;

import com.ulyp.core.*;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;
import com.ulyp.transport.TMethodEnterTraceDecoder;
import com.ulyp.transport.TMethodExitTraceDecoder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.*;

public class StoringService {

    private final Storage storage;

    public StoringService(Storage storage) {
        this.storage = storage;
    }

    public MethodTraceTreeNode store(MethodEnterTraceList enterTracesList, MethodExitTraceList exitTracesList, MethodDescriptionList methodDescriptionList) {
        Long2ObjectMap<TMethodDescriptionDecoder> methodDescriptionMap = new Long2ObjectOpenHashMap<>();
        Iterator<TMethodDescriptionDecoder> iterator = methodDescriptionList.copyingIterator();
        while (iterator.hasNext()) {
            TMethodDescriptionDecoder methodDescription = iterator.next();
            methodDescriptionMap.put(methodDescription.id(), methodDescription);
        }

        Iterator<TMethodEnterTraceDecoder> enterTraceIt = enterTracesList.iterator();
        Iterator<TMethodExitTraceDecoder> exitTraceIt = exitTracesList.iterator();

        TMethodEnterTraceDecoder currentEnterTrace = enterTraceIt.next();
        TMethodExitTraceDecoder currentExitTrace = exitTraceIt.next();

        NodeBuilder root = new NodeBuilder(null, methodDescriptionMap.get(currentEnterTrace.methodId()), currentEnterTrace);
        currentEnterTrace = enterTraceIt.hasNext() ? enterTraceIt.next() : null;

        Deque<NodeBuilder> rootPath = new ArrayDeque<>();
        rootPath.add(root);

        for (; currentEnterTrace != null || currentExitTrace != null; ) {
            NodeBuilder currentNode = rootPath.getLast();

            long currentCallId = currentNode.callId;
            if (currentExitTrace != null && currentExitTrace.callId() == currentCallId) {
                currentNode.setExitTraceData(currentExitTrace);
                currentExitTrace = exitTraceIt.hasNext() ? exitTraceIt.next() : null;
                currentNode.persist();
                rootPath.removeLast();
            } else if (currentEnterTrace != null) {
                NodeBuilder next = new NodeBuilder(currentNode, methodDescriptionMap.get(currentEnterTrace.methodId()), currentEnterTrace);
                currentEnterTrace = enterTraceIt.hasNext() ? enterTraceIt.next() : null;
                rootPath.add(next);
            } else {
                throw new RuntimeException("Inconsistent state");
            }
        }

        return root.persisted;
    }

    private class NodeBuilder {

        private final NodeBuilder parent;
        private final TMethodDescriptionDecoder methodDescription;
        private final long callId;
        private final List<String> args;
        private final List<MethodTraceTreeNode> children = new ArrayList<>();

        private MethodTraceTreeNode persisted;
        private String returnValue;
        private boolean thrown;

        private NodeBuilder(NodeBuilder parent, TMethodDescriptionDecoder methodDescription, TMethodEnterTraceDecoder decoder) {
            this.parent = parent;
            this.methodDescription = methodDescription;
            this.callId = decoder.callId();

            this.args = new ArrayList<>();
            TMethodEnterTraceDecoder.ArgumentsDecoder arguments = decoder.arguments();
            while (arguments.hasNext()) {
                arguments = arguments.next();
                args.add(arguments.value());
            }
        }

        private void setExitTraceData(TMethodExitTraceDecoder decoder) {
            this.returnValue = decoder.returnValue();
            this.thrown = decoder.thrown() == BooleanType.T;
        }

        public void persist() {
            MethodTraceTreeNode node = new MethodTraceTreeNode(
                    args,
                    returnValue,
                    thrown,
                    methodDescription,
                    children
            );
            storage.persist(node);
            if (parent != null) {
                parent.children.add(node);
            }
            persisted = node;
        }
    }
}
