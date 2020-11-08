package com.ulyp.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.ulyp.core.printers.*;
import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;

public class CallRecordTreeDeserializer {

    private final CallRecordDatabase database;

    private final Long2ObjectMap<TypeInfo> classIdMap = new Long2ObjectOpenHashMap<>();
    private final DecodingContext decodingContext = new DecodingContext(classIdMap);

    private CallRecordBuilder root = null;
    private final Deque<CallRecordBuilder> rootPath = new ArrayDeque<>();

    public CallRecordTreeDeserializer(CallRecordDatabase database) {
        this.database = database;
    }

    public boolean hasCompleteTree() {
        return getRoot().isComplete();
    }

    public CallRecord getRoot() {
        return root.persist();
    }

    public CallRecord deserialize(
            CallEnterRecordList enterRecordsList,
            CallExitRecordList exitRecordsList,
            MethodInfoList methodInfoList,
            List<TClassDescription> classDescriptionList)
    {
        for (TClassDescription classDescription : classDescriptionList) {
            classIdMap.put(
                    classDescription.getId(),
                    new NameOnlyTypeInfo(classDescription.getId(), classDescription.getName())
            );
        }

        Long2ObjectMap<TMethodInfoDecoder> methodDescriptionMap = new Long2ObjectOpenHashMap<>();
        Iterator<TMethodInfoDecoder> iterator = methodInfoList.copyingIterator();
        while (iterator.hasNext()) {
            TMethodInfoDecoder methodDescription = iterator.next();
            methodDescriptionMap.put(methodDescription.id(), methodDescription);
        }

        PeekingIterator<TCallEnterRecordDecoder> enterRecordIt = Iterators.peekingIterator(enterRecordsList.iterator());
        PeekingIterator<TCallExitRecordDecoder> exitRecordIt = Iterators.peekingIterator(exitRecordsList.iterator());

        if (this.root == null) {
            TCallEnterRecordDecoder enterRecord = enterRecordIt.next();
            if (enterRecord.callId() != 0) {
                throw new RuntimeException("Call id of the root must be 0");
            }
            root = new CallRecordBuilder(null, methodDescriptionMap.get(enterRecord.methodId()), enterRecord);
            rootPath.add(root);
        }

        while (enterRecordIt.hasNext() || exitRecordIt.hasNext()) {
            CallRecordBuilder currentNode = rootPath.getLast();

            long currentCallId = currentNode.callId;
            if (exitRecordIt.hasNext() && exitRecordIt.peek().callId() == currentCallId) {
                currentNode.setExitRecordData(exitRecordIt.next());
                currentNode.persist();
                rootPath.removeLast();
            } else if (enterRecordIt.hasNext()) {
                TCallEnterRecordDecoder enterRecord = enterRecordIt.next();
                CallRecordBuilder next = new CallRecordBuilder(currentNode, methodDescriptionMap.get(enterRecord.methodId()), enterRecord);
                rootPath.add(next);
            } else {
                throw new RuntimeException("Inconsistent state");
            }
        }

        for (CallRecordBuilder node : rootPath) {
            node.persist();
        }

        return root.persist();
    }

    private class CallRecordBuilder {

        private final CallRecordBuilder parent;
        private final TMethodInfoDecoder methodDescription;
        private final long callId;
        private final ObjectRepresentation callee;
        private final List<ObjectRepresentation> args;
        private final LongList childrenIds = new LongArrayList();

        private CallRecord persisted;
        private ObjectRepresentation returnValue = NotRecordedObjectRepresentation.getInstance();
        private boolean thrown;

        private CallRecordBuilder(
                CallRecordBuilder parent,
                TMethodInfoDecoder methodDescription,
                TCallEnterRecordDecoder decoder)
        {
            this.parent = parent;
            this.methodDescription = methodDescription;
            this.callId = decoder.callId();

            this.args = new ArrayList<>();
            TCallEnterRecordDecoder.ArgumentsDecoder arguments = decoder.arguments();
            while (arguments.hasNext()) {
                arguments = arguments.next();
                UnsafeBuffer buffer = new UnsafeBuffer();
                arguments.wrapValue(buffer);
                args.add(ObjectBinaryPrinterType.printerForId(arguments.printerId()).read(
                        classIdMap.get(arguments.classId()),
                        new BinaryInputImpl(buffer),
                        decodingContext)
                );
            }

            UnsafeBuffer buffer = new UnsafeBuffer();
            decoder.wrapCallee(buffer);

            TypeInfo calleeTypeInfo = classIdMap.get(decoder.calleeClassId());

            this.callee = ObjectBinaryPrinterType.printerForId(decoder.calleePrinterId()).read(
                    calleeTypeInfo,
                    new BinaryInputImpl(buffer),
                    decodingContext
            );
        }

        private void setExitRecordData(TCallExitRecordDecoder decoder) {
            UnsafeBuffer returnValueBuffer = new UnsafeBuffer();
            decoder.wrapReturnValue(returnValueBuffer);
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(decoder.returnPrinterId());
            this.returnValue = printer.read(classIdMap.get(decoder.returnClassId()), new BinaryInputImpl(returnValueBuffer), decodingContext);
            this.thrown = decoder.thrown() == BooleanType.T;
        }

        public CallRecord persist() {
            int subtreeNodeCount = childrenIds.stream()
                    .map(database::find)
                    .map(CallRecord::getSubtreeNodeCount)
                    .reduce(1, Integer::sum);
            CallRecord node;
            if (persisted != null) {
                node = persisted;
            } else {
                node = new CallRecord(
                        callee,
                        args,
                        methodDescription,
                        database,
                        subtreeNodeCount
                );
            }
            node.setReturnValue(this.returnValue);
            node.setThrown(this.thrown);
            node.setSubtreeNodeCount(subtreeNodeCount);

            database.persist(node);
            persisted = node;
            childrenIds.forEach((long childId) -> database.linkChild(node.getId(), childId));
            if (parent != null) {
                parent.childrenIds.add(node.getId());
            }
            return persisted;
        }
    }
}
