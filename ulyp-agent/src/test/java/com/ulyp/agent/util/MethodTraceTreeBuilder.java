package com.ulyp.agent.util;

import com.ulyp.core.*;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;
import com.ulyp.transport.TMethodEnterTraceDecoder;
import com.ulyp.transport.TMethodExitTraceDecoder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MethodTraceTreeBuilder {

    public static MethodTraceTree from(
            MethodEnterTraceList enterTracesList,
            MethodExitTraceList exitTracesList,
            MethodDescriptionList methodDescriptionList)
    {
        return new Builder(enterTracesList, exitTracesList, methodDescriptionList).build();
    }

    private static class NodeBuilder {

        private final NodeBuilder parent;
        private final TMethodDescriptionDecoder methodDescription;
        private final long callId;
        private final List<String> args;
        private String returnValue;
        private boolean thrown;
        private final List<NodeBuilder> children = new ArrayList<>();

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

        private NodeBuilder addChild(TMethodDescriptionDecoder methodDescription, TMethodEnterTraceDecoder enterTrace) {
            NodeBuilder child = new NodeBuilder(this, methodDescription, enterTrace);
            children.add(child);
            return child;
        }

        private void setExitTraceData(TMethodExitTraceDecoder decoder) {
            this.returnValue = decoder.returnValue();
            this.thrown = decoder.thrown() == BooleanType.T;
        }

        private MethodTraceTreeNode build() {
            List<MethodTraceTreeNode> children = new ArrayList<>();
            int nodeCount = 1;
            for (NodeBuilder child : this.children) {
                MethodTraceTreeNode builtNode = child.build();
                nodeCount += builtNode.getNodeCount();
                children.add(builtNode);
            }

            return new MethodTraceTreeNode(
                    args,
                    returnValue,
                    thrown,
                    methodDescription,
                    children,
                    nodeCount
            );
        }
    }

    private static class Builder {

        private final MethodEnterTraceList enterTracesList;
        private final MethodExitTraceList exitTracesList;
        private final Long2ObjectMap<TMethodDescriptionDecoder> methodIdToInfoMap;

        private Builder(MethodEnterTraceList enterTracesList, MethodExitTraceList exitTracesList, MethodDescriptionList methodDescriptionList) {
            this.enterTracesList = enterTracesList;
            this.exitTracesList = exitTracesList;
            this.methodIdToInfoMap = new Long2ObjectOpenHashMap<>(methodDescriptionList.size() * 2);

            Iterator<TMethodDescriptionDecoder> iterator = methodDescriptionList.copyingIterator();

            while (iterator.hasNext()) {
                TMethodDescriptionDecoder methodDescription = iterator.next();
                this.methodIdToInfoMap.put(methodDescription.id(), methodDescription);
            }
        }

        private MethodTraceTree build() {
            Iterator<TMethodEnterTraceDecoder> enterTraceIt = enterTracesList.iterator();
            Iterator<TMethodExitTraceDecoder> exitTraceIt = exitTracesList.iterator();

            TMethodEnterTraceDecoder currentEnterTrace = enterTraceIt.next();
            TMethodExitTraceDecoder currentExitTrace = exitTraceIt.next();

            NodeBuilder root = new NodeBuilder(null, methodIdToInfoMap.get(currentEnterTrace.methodId()), currentEnterTrace);
            currentEnterTrace = enterTraceIt.hasNext() ? enterTraceIt.next() : null;

            NodeBuilder currentNode = root;

            for (; currentEnterTrace != null || currentExitTrace != null; ) {
                long currentCallId = currentNode.callId;
                if (currentExitTrace != null && currentExitTrace.callId() == currentCallId) {
                    currentNode.setExitTraceData(currentExitTrace);
                    currentExitTrace = exitTraceIt.hasNext() ? exitTraceIt.next() : null;
                    currentNode = currentNode.parent;
                } else if (currentEnterTrace != null) {
                    currentNode = currentNode.addChild(methodIdToInfoMap.get(currentEnterTrace.methodId()), currentEnterTrace);
                    currentEnterTrace = enterTraceIt.hasNext() ? enterTraceIt.next() : null;
                } else {
                    throw new RuntimeException("Inconsistent state");
                }
            }
            return new MethodTraceTree(root.build());
        }
    }
}
