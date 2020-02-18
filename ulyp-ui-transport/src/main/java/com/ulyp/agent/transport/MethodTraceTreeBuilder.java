package com.ulyp.agent.transport;

import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodInfo;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public class MethodTraceTreeBuilder {

    public static MethodTraceTree from(TMethodTraceLogUploadRequest request) {
        return new Builder(request).build();
    }

    private static class NodeBuilder {

        private final NodeBuilder parent;
        private final TMethodInfo methodInfo;
        private final TMethodEnterTrace methodEnterTrace;
        private TMethodExitTrace methodExitTrace;
        private final List<NodeBuilder> children = new ArrayList<>();

        private NodeBuilder(NodeBuilder parent, TMethodInfo methodInfo, TMethodEnterTrace methodEnterTrace) {
            this.parent = parent;
            this.methodInfo = methodInfo;
            this.methodEnterTrace = methodEnterTrace;
        }

        private NodeBuilder addChild(TMethodInfo methodInfo, TMethodEnterTrace methodEnterTrace) {
            NodeBuilder child = new NodeBuilder(this, methodInfo, methodEnterTrace);
            children.add(child);
            return child;
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
                    methodEnterTrace,
                    methodExitTrace,
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
            List<TMethodEnterTrace> enterTracesList = request.getTraceLog().getEnterTracesList();
            List<TMethodExitTrace> exitTracesList = request.getTraceLog().getExitTracesList();

            NodeBuilder root = new NodeBuilder(null, methodIdToInfoMap.get(enterTracesList.get(0).getMethodId()), enterTracesList.get(0));
            NodeBuilder currentNode = root;
            int enterIndex = 1, exitIndex = 0;

            for (; enterIndex < enterTracesList.size() || exitIndex < exitTracesList.size(); ) {
                long currentCallId = currentNode.methodEnterTrace.getCallId();
                if (exitTracesList.get(exitIndex).getCallId() == currentCallId) {
                    currentNode.methodExitTrace = exitTracesList.get(exitIndex++);
                    currentNode = currentNode.parent;
                } else {
                    currentNode = currentNode.addChild(methodIdToInfoMap.get(enterTracesList.get(enterIndex).getMethodId()), enterTracesList.get(enterIndex));
                    enterIndex++;
                }
            }
            return new MethodTraceTree(root.build());
        }
    }
}
