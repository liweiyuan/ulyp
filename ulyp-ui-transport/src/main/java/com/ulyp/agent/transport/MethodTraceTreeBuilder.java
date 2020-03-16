package com.ulyp.agent.transport;

import com.ulyp.core.MethodEnterTraceList;
import com.ulyp.core.MethodExitTraceList;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MethodTraceTreeBuilder {

    public static MethodTraceTree from(TMethodTraceLogUploadRequest request) {
        return new Builder(request).build();
    }

    private static class NodeBuilder {

        private final NodeBuilder parent;
        private final TMethodInfo methodInfo;
        private final long callId;
        private final List<String> args;
        private String returnValue;
        private boolean thrown;
        private final List<NodeBuilder> children = new ArrayList<>();

        private NodeBuilder(NodeBuilder parent, TMethodInfo methodInfo, SMethodEnterTraceDecoder decoder) {
            this.parent = parent;
            this.methodInfo = methodInfo;
            this.callId = decoder.callId();

            this.args = new ArrayList<>();
            SMethodEnterTraceDecoder.ArgumentsDecoder arguments = decoder.arguments();
            while (arguments.hasNext()) {
                arguments = arguments.next();
                args.add(arguments.value());
            }
        }

        private NodeBuilder addChild(TMethodInfo methodInfo, SMethodEnterTraceDecoder enterTrace) {
            NodeBuilder child = new NodeBuilder(this, methodInfo, enterTrace);
            children.add(child);
            return child;
        }

        private void setExitTraceData(SMethodExitTraceDecoder decoder) {
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
                    methodInfo,
                    children,
                    nodeCount
            );
        }
    }

    private static class Builder {

        private final TMethodTraceLogUploadRequest request;
        private final Long2ObjectMap<TMethodInfo> methodIdToInfoMap;

        private Builder(TMethodTraceLogUploadRequest request) {
            this.request = request;
            this.methodIdToInfoMap = new Long2ObjectOpenHashMap<>(request.getMethodInfosCount() * 2);

            for (TMethodInfo methodInfo : request.getMethodInfosList()) {
                this.methodIdToInfoMap.put(methodInfo.getId(), methodInfo);
            }
        }

        private MethodTraceTree build() {
            MethodEnterTraceList enterTracesList = new MethodEnterTraceList(request.getTraceLog().getEnterTraces());
            MethodExitTraceList exitTracesList = new MethodExitTraceList(request.getTraceLog().getExitTraces());

            Iterator<SMethodEnterTraceDecoder> enterTraceIt = enterTracesList.iterator();
            Iterator<SMethodExitTraceDecoder> exitTraceIt = exitTracesList.iterator();

            SMethodEnterTraceDecoder currentEnterTrace = enterTraceIt.next();
            SMethodExitTraceDecoder currentExitTrace = exitTraceIt.next();

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
