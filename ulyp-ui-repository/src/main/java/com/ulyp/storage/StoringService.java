package com.ulyp.storage;

import com.ulyp.core.*;
import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;

public class StoringService {

    private final MethodEnterTraceList enterTracesList;
    private final MethodExitTraceList exitTracesList;
    private final MethodDescriptionList methodDescriptionList;
    private final Long2ObjectMap<ClassDescription> classIdMap;
    private final Storage storage;

    public StoringService(MethodEnterTraceList enterTracesList,
                          MethodExitTraceList exitTracesList,
                          MethodDescriptionList methodDescriptionList,
                          ClassDescriptionList classDescriptionList,
                          Storage storage)
    {
        this.storage = storage;
        this.enterTracesList = enterTracesList;
        this.exitTracesList = exitTracesList;
        this.methodDescriptionList = methodDescriptionList;

        this.classIdMap = new Long2ObjectOpenHashMap<>();
        for (TClassDescriptionDecoder classDescription : classDescriptionList) {
            this.classIdMap.put(
                    classDescription.id(),
                    new ClassDescription(classDescription.id(), classDescription.simpleClassName(), classDescription.className())
            );
        }
    }

    public MethodTraceTreeNode store()
    {
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
        private final List<ObjectValue> args;
        private final List<MethodTraceTreeNode> children = new ArrayList<>();

        private MethodTraceTreeNode persisted;
        private ObjectValue returnValue;
        private boolean thrown;

        private NodeBuilder(NodeBuilder parent, TMethodDescriptionDecoder methodDescription, TMethodEnterTraceDecoder decoder) {
            this.parent = parent;
            this.methodDescription = methodDescription;
            this.callId = decoder.callId();

            this.args = new ArrayList<>();
            TMethodEnterTraceDecoder.ArgumentsDecoder arguments = decoder.arguments();
            while (arguments.hasNext()) {
                arguments = arguments.next();
                UnsafeBuffer buffer = new UnsafeBuffer();
                arguments.wrapValue(buffer);
                args.add(new ObjectValue(
                        ObjectBinaryPrinterType.printerForId(arguments.printerId()).read(classIdMap.get(arguments.classId()), new BinaryInputImpl(buffer)),
                        classIdMap.get(arguments.classId())
                ));
            }
        }

        private void setExitTraceData(TMethodExitTraceDecoder decoder) {
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(decoder.returnPrinterId());
            UnsafeBuffer returnValueBuffer = new UnsafeBuffer();
            decoder.wrapReturnValue(returnValueBuffer);
            this.returnValue = new ObjectValue(printer.read(classIdMap.get(decoder.returnClassId()), new BinaryInputImpl(returnValueBuffer)), classIdMap.get(decoder.returnClassId()));
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
