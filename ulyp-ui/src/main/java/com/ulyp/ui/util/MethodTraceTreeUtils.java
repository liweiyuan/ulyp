package com.ulyp.ui.util;

import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodInfo;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.List;

public class MethodTraceTreeUtils {

    public static MethodTraceTree from(TMethodTraceLogUploadRequest request) {
        return new Builder(request).build();
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

        public MethodTraceTree build() {

            List<TMethodEnterTrace> enterTracesList = request.getTraceLog().getEnterTracesList();
            List<TMethodExitTrace> exitTracesList = request.getTraceLog().getExitTracesList();

            MethodTraceTreeNode root = new MethodTraceTreeNode(null, enterTracesList.get(0), methodIdToInfoMap.get(enterTracesList.get(0).getMethodId()));
            MethodTraceTreeNode currentNode = root;
            int enterIndex = 1, exitIndex = 0;

            for (; enterIndex < enterTracesList.size() || exitIndex < exitTracesList.size(); ) {
                long currentCallId = currentNode.getCallId();
                if (exitTracesList.get(exitIndex).getCallId() == currentCallId) {
                    currentNode.setMethodExitTrace(exitTracesList.get(exitIndex++));
                    currentNode = currentNode.getParent();
                } else {
                    MethodTraceTreeNode newNode = new MethodTraceTreeNode(currentNode, enterTracesList.get(enterIndex), methodIdToInfoMap.get(enterTracesList.get(enterIndex).getMethodId()));
                    currentNode.addChild(newNode);
                    currentNode = newNode;
                    enterIndex++;
                }
            }
            return new MethodTraceTree(root);
        }
    }
}
