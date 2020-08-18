package com.ulyp.core;

import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;

public class CallRecordTreeDao {

    private final CallEnterRecordList enterRecordsList;
    private final CallExitRecordList exitRecordsList;
    private final MethodDescriptionList methodDescriptionList;
    private final Long2ObjectMap<ClassDescription> classIdMap;
    private final DecodingContext decodingContext;
    private final CallRecordDatabase database;

    public CallRecordTreeDao(CallEnterRecordList enterRecordsList,
                             CallExitRecordList exitRecordsList,
                             MethodDescriptionList methodDescriptionList,
                             ClassDescriptionList classDescriptionList,
                             CallRecordDatabase database)
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

        return new CallRecordTree(root.persisted);
    }

    private class CallRecordBuilder {

        private final CallRecordBuilder parent;
        private final TMethodDescriptionDecoder methodDescription;
        private final long callId;
        private final ObjectRepresentation callee;
        private final List<ObjectRepresentation> args;
        private final List<CallRecord> children = new ArrayList<>();

        private CallRecord persisted;
        private ObjectRepresentation returnValue;
        private boolean thrown;

        private CallRecordBuilder(CallRecordBuilder parent, TMethodDescriptionDecoder methodDescription, TCallEnterRecordDecoder decoder) {
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

            ClassDescription calleeType = classIdMap.get(decoder.calleeClassId());

            this.callee = ObjectBinaryPrinterType.printerForId(decoder.calleePrinterId()).read(
                    calleeType,
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
