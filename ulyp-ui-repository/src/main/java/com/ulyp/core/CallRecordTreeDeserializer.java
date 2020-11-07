package com.ulyp.core;

import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;

public class CallRecordTreeDeserializer {

    private final CallEnterRecordList enterRecordsList;
    private final CallExitRecordList exitRecordsList;
    private final MethodInfoList methodInfoList;
    private final DecodingContext decodingContext;
    private final CallRecordDatabase database;
    private final Long2ObjectMap<TypeInfo> classIdMap;

    public CallRecordTreeDeserializer(CallEnterRecordList enterRecordsList,
                                      CallExitRecordList exitRecordsList,
                                      MethodInfoList methodInfoList,
                                      List<TClassDescription> classDescriptionList,
                                      CallRecordDatabase database)
    {
        this.database = database;
        this.enterRecordsList = enterRecordsList;
        this.exitRecordsList = exitRecordsList;
        this.methodInfoList = methodInfoList;

        this.classIdMap = new Long2ObjectOpenHashMap<>();
        for (TClassDescription classDescription : classDescriptionList) {
            this.classIdMap.put(
                    classDescription.getId(),
                    new NameOnlyTypeInfo(classDescription.getId(), classDescription.getName())
            );
        }
        this.decodingContext = new DecodingContext(classIdMap);
    }

    public CallRecord get() {
        Long2ObjectMap<TMethodInfoDecoder> methodDescriptionMap = new Long2ObjectOpenHashMap<>();
        Iterator<TMethodInfoDecoder> iterator = methodInfoList.copyingIterator();
        while (iterator.hasNext()) {
            TMethodInfoDecoder methodDescription = iterator.next();
            methodDescriptionMap.put(methodDescription.id(), methodDescription);
        }

        Iterator<TCallEnterRecordDecoder> enterRecordIt = enterRecordsList.iterator();
        Iterator<TCallExitRecordDecoder> exitRecordIt = exitRecordsList.iterator();

        TCallEnterRecordDecoder currentEnterRecord = enterRecordIt.next();
        TCallExitRecordDecoder currentExitRecord = exitRecordIt.next();

        CallRecordBuilder root = new CallRecordBuilder(null, methodDescriptionMap.get(currentEnterRecord.methodId()), currentEnterRecord);
        currentEnterRecord = enterRecordIt.hasNext() ? enterRecordIt.next() : null;

        Deque<CallRecordBuilder> rootPath = new ArrayDeque<>();
        rootPath.add(root);

        while (currentEnterRecord != null || currentExitRecord != null) {
            CallRecordBuilder currentNode = rootPath.getLast();

            long currentCallId = currentNode.callId;
            if (currentExitRecord != null && currentExitRecord.callId() == currentCallId) {
                currentNode.setExitRecordData(currentExitRecord);
                currentExitRecord = exitRecordIt.hasNext() ? exitRecordIt.next() : null;
                currentNode.persist();
                rootPath.removeLast();
            } else if (currentEnterRecord != null) {
                CallRecordBuilder next = new CallRecordBuilder(currentNode, methodDescriptionMap.get(currentEnterRecord.methodId()), currentEnterRecord);
                currentEnterRecord = enterRecordIt.hasNext() ? enterRecordIt.next() : null;
                rootPath.add(next);
            } else {
                throw new RuntimeException("Inconsistent state");
            }
        }

        return root.persisted;
    }

    private class CallRecordBuilder {

        private final CallRecordBuilder parent;
        private final TMethodInfoDecoder methodDescription;
        private final long callId;
        private final ObjectRepresentation callee;
        private final List<ObjectRepresentation> args;
        private final List<CallRecord> children = new ArrayList<>();

        private CallRecord persisted;
        private ObjectRepresentation returnValue;
        private boolean thrown;

        private CallRecordBuilder(CallRecordBuilder parent, TMethodInfoDecoder methodDescription, TCallEnterRecordDecoder decoder) {
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

        public void persist() {
            int subtreeNodeCount = children.stream().map(CallRecord::getSubtreeNodeCount).reduce(1, Integer::sum);

            CallRecord node = new CallRecord(
                    callee,
                    args,
                    returnValue,
                    thrown,
                    methodDescription,
                    database,
                    subtreeNodeCount
            );
            database.persist(node);
            children.forEach(child -> database.linkChild(node.getId(), child.getId()));
            if (parent != null) {
                parent.children.add(node);
            }
            persisted = node;
        }
    }
}
