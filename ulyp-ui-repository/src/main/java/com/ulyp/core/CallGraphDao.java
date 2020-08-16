package com.ulyp.core;

import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;

public class CallGraphDao {

    private final CallEnterRecordList enterRecordsList;
    private final CallExitRecordList exitRecordsList;
    private final MethodDescriptionList methodDescriptionList;
    private final Long2ObjectMap<ClassDescription> classIdMap;
    private final DecodingContext decodingContext;
    private final CallGraphDatabase database;

    public CallGraphDao(CallEnterRecordList enterRecordsList,
                        CallExitRecordList exitRecordsList,
                        MethodDescriptionList methodDescriptionList,
                        ClassDescriptionList classDescriptionList,
                        CallGraphDatabase database)
    {
        this.database = database;
        this.enterRecordsList = enterRecordsList;
        this.exitRecordsList = exitRecordsList;
        this.methodDescriptionList = methodDescriptionList;

        this.classIdMap = new Long2ObjectOpenHashMap<>();
        this.decodingContext = new DecodingContext(classIdMap);
        for (TClassDescriptionDecoder classDescription : classDescriptionList) {
            this.classIdMap.put(
                    classDescription.id(),
                    new ClassDescription(classDescription.id(), classDescription.simpleClassName(), classDescription.className())
            );
        }
    }

    public CallRecordTree getCallRecordTree() {
        Long2ObjectMap<TMethodDescriptionDecoder> methodDescriptionMap = new Long2ObjectOpenHashMap<>();
        Iterator<TMethodDescriptionDecoder> iterator = methodDescriptionList.copyingIterator();
        while (iterator.hasNext()) {
            TMethodDescriptionDecoder methodDescription = iterator.next();
            methodDescriptionMap.put(methodDescription.id(), methodDescription);
        }

        Iterator<TCallEnterRecordDecoder> enterRecordIt = enterRecordsList.iterator();
        Iterator<TCallExitRecordDecoder> exitRecordIt = exitRecordsList.iterator();

        TCallEnterRecordDecoder currentEnterRecord = enterRecordIt.next();
        TCallExitRecordDecoder currentExitRecord = exitRecordIt.next();

        CallBuilder root = new CallBuilder(null, methodDescriptionMap.get(currentEnterRecord.methodId()), currentEnterRecord);
        currentEnterRecord = enterRecordIt.hasNext() ? enterRecordIt.next() : null;

        Deque<CallBuilder> rootPath = new ArrayDeque<>();
        rootPath.add(root);

        for (; currentEnterRecord != null || currentExitRecord != null; ) {
            CallBuilder currentNode = rootPath.getLast();

            long currentCallId = currentNode.callId;
            if (currentExitRecord != null && currentExitRecord.callId() == currentCallId) {
                currentNode.setExitRecordData(currentExitRecord);
                currentExitRecord = exitRecordIt.hasNext() ? exitRecordIt.next() : null;
                currentNode.persist();
                rootPath.removeLast();
            } else if (currentEnterRecord != null) {
                CallBuilder next = new CallBuilder(currentNode, methodDescriptionMap.get(currentEnterRecord.methodId()), currentEnterRecord);
                currentEnterRecord = enterRecordIt.hasNext() ? enterRecordIt.next() : null;
                rootPath.add(next);
            } else {
                throw new RuntimeException("Inconsistent state");
            }
        }

        return new CallRecordTree(root.persisted);
    }

    private class CallBuilder {

        private final CallBuilder parent;
        private final TMethodDescriptionDecoder methodDescription;
        private final long callId;
        private final ObjectValue callee;
        private final List<ObjectValue> args;
        private final List<CallRecord> children = new ArrayList<>();

        private CallRecord persisted;
        private ObjectValue returnValue;
        private boolean thrown;

        private CallBuilder(CallBuilder parent, TMethodDescriptionDecoder methodDescription, TCallEnterRecordDecoder decoder) {
            this.parent = parent;
            this.methodDescription = methodDescription;
            this.callId = decoder.callId();

            this.args = new ArrayList<>();
            TCallEnterRecordDecoder.ArgumentsDecoder arguments = decoder.arguments();
            while (arguments.hasNext()) {
                arguments = arguments.next();
                UnsafeBuffer buffer = new UnsafeBuffer();
                arguments.wrapValue(buffer);
                args.add(new ObjectValue(
                        ObjectBinaryPrinterType.printerForId(arguments.printerId()).read(
                                classIdMap.get(arguments.classId()),
                                new BinaryInputImpl(buffer),
                                decodingContext),
                        classIdMap.get(arguments.classId())
                ));
            }

            UnsafeBuffer buffer = new UnsafeBuffer();
            decoder.wrapCallee(buffer);

            ClassDescription calleeType = classIdMap.get(decoder.calleeClassId());

            this.callee = new ObjectValue(
                    ObjectBinaryPrinterType.printerForId(decoder.calleePrinterId()).read(
                            calleeType,
                            new BinaryInputImpl(buffer),
                            decodingContext),
                    calleeType
            );
        }

        private void setExitRecordData(TCallExitRecordDecoder decoder) {
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(decoder.returnPrinterId());
            UnsafeBuffer returnValueBuffer = new UnsafeBuffer();
            decoder.wrapReturnValue(returnValueBuffer);
            this.returnValue = new ObjectValue(
                    printer.read(classIdMap.get(decoder.returnClassId()), new BinaryInputImpl(returnValueBuffer), decodingContext),
                    classIdMap.get(decoder.returnClassId()));
            this.thrown = decoder.thrown() == BooleanType.T;
        }

        public void persist() {
            CallRecord node = new CallRecord(
                    callee,
                    args,
                    returnValue,
                    thrown,
                    methodDescription,
                    children
            );
            database.persist(node);
            if (parent != null) {
                parent.children.add(node);
            }
            persisted = node;
        }
    }
}
