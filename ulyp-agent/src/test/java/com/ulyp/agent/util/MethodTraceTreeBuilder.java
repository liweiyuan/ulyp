package com.ulyp.agent.util;

import com.ulyp.core.*;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MethodTraceTreeBuilder {

    private final MethodEnterTraceList enterTracesList;
    private final MethodExitTraceList exitTracesList;
    private final Long2ObjectMap<TMethodDescriptionDecoder> methodIdToInfoMap;
    private final Long2ObjectMap<ClassDescription> classIdMap;

    public MethodTraceTreeBuilder(TMethodTraceLogUploadRequest request) {
        this.enterTracesList = new MethodEnterTraceList(request.getTraceLog().getEnterTraces());
        this.exitTracesList = new MethodExitTraceList(request.getTraceLog().getExitTraces());

        MethodDescriptionList methodDescriptionList = new MethodDescriptionList(request.getMethodDescriptionList().getData());

        this.methodIdToInfoMap = new Long2ObjectOpenHashMap<>(methodDescriptionList.size() * 2);
        this.classIdMap = new Long2ObjectOpenHashMap<>();

        Iterator<TMethodDescriptionDecoder> iterator = methodDescriptionList.copyingIterator();
        while (iterator.hasNext()) {
            TMethodDescriptionDecoder methodDescription = iterator.next();
            this.methodIdToInfoMap.put(methodDescription.id(), methodDescription);
        }

        ClassDescriptionList classDescriptionList = new ClassDescriptionList(request.getClassDescriptionList().getData());
        for (TClassDescriptionDecoder classDescription : classDescriptionList) {
            this.classIdMap.put(
                    classDescription.id(),
                    new ClassDescription(classDescription.id(), classDescription.simpleClassName(), classDescription.className())
            );
        }
    }

    public MethodTraceTree build() {
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

    private class NodeBuilder {

        private final NodeBuilder parent;
        private final TMethodDescriptionDecoder methodDescription;
        private final long callId;
        private final List<String> args;
        private final List<ClassDescription> argTypes;
        private String returnValue;
        private boolean thrown;
        private final List<NodeBuilder> children = new ArrayList<>();

        private NodeBuilder(NodeBuilder parent, TMethodDescriptionDecoder methodDescription, TMethodEnterTraceDecoder decoder) {
            this.parent = parent;
            this.methodDescription = methodDescription;
            this.callId = decoder.callId();

            this.args = new ArrayList<>();
            this.argTypes = new ArrayList<>();
            TMethodEnterTraceDecoder.ArgumentsDecoder arguments = decoder.arguments();
            while (arguments.hasNext()) {
                arguments = arguments.next();
                argTypes.add(classIdMap.get(arguments.classId()));

                UnsafeBuffer buffer = new UnsafeBuffer();
                arguments.wrapValue(buffer);
                BinaryInputImpl binaryInput = new BinaryInputImpl(buffer);
                args.add(ObjectBinaryPrinterType.printerForId(arguments.printerId()).read(classIdMap.get(arguments.classId()), binaryInput));
            }
        }

        private NodeBuilder addChild(TMethodDescriptionDecoder methodDescription, TMethodEnterTraceDecoder enterTrace) {
            NodeBuilder child = new NodeBuilder(this, methodDescription, enterTrace);
            children.add(child);
            return child;
        }

        private void setExitTraceData(TMethodExitTraceDecoder decoder) {
            UnsafeBuffer buffer = new UnsafeBuffer();
            decoder.wrapReturnValue(buffer);
            BinaryInputImpl binaryInput = new BinaryInputImpl(buffer);
            returnValue = ObjectBinaryPrinterType.printerForId(decoder.returnPrinterId()).read(classIdMap.get(decoder.returnClassId()), binaryInput);
            this.thrown = decoder.thrown() == BooleanType.T;
        }

        private MethodTraceTreeNode build() {
            List<MethodTraceTreeNode> children = new ArrayList<>();
            for (NodeBuilder child : this.children) {
                MethodTraceTreeNode builtNode = child.build();
                children.add(builtNode);
            }

            return new MethodTraceTreeNode(
                    args,
                    argTypes,
                    returnValue,
                    thrown,
                    methodDescription,
                    children
            );
        }
    }
}
