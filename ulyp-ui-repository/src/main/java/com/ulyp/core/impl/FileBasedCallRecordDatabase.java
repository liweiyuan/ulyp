package com.ulyp.core.impl;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.ulyp.core.*;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import org.agrona.concurrent.UnsafeBuffer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FileBasedCallRecordDatabase implements CallRecordDatabase {

    private final File enterRecordsFile;
    private final File exitRecordsFile;
    private final OutputStream enterRecordsOutputStream;
    private final RandomAccessFile enterRecordRandomAccess;
    private final RandomAccessFile exitRecordRandomAccess;
    private final OutputStream exitRecordsOutputStream;

    private long enterPos = 0;
    private long exitPos = 0;
    private final Int2ObjectMap<LongList> children = new Int2ObjectOpenHashMap<>();
    private final Int2LongMap enterRecordPos = new Int2LongOpenHashMap() {
        @Override
        public long defaultReturnValue() {
            return -1;
        }
    };
    private final Int2LongMap exitRecordPos = new Int2LongOpenHashMap();
    private final Long2ObjectMap<TypeInfo> classIdMap = new Long2ObjectOpenHashMap<>();
    private final DecodingContext decodingContext = new DecodingContext(classIdMap);
    private final Long2ObjectMap<TMethodInfoDecoder> methodDescriptionMap = new Long2ObjectOpenHashMap<>();
    private final LongStack currentRootStack = new LongArrayList();
    private final byte[] tmpBuf = new byte[512 * 1024];

    public FileBasedCallRecordDatabase(File file) {

        exitRecordPos.defaultReturnValue(-1L);
        enterRecordPos.defaultReturnValue(-1L);

        try {
            this.enterRecordsFile = new File("C:\\Temp\\db-enter-records.dat");
            this.exitRecordsFile = new File("C:\\Temp\\db-exit-records.dat");
            this.enterRecordsOutputStream = new BufferedOutputStream(new FileOutputStream(enterRecordsFile, false));
            this.exitRecordsOutputStream = new BufferedOutputStream(new FileOutputStream(exitRecordsFile, false));
            this.enterRecordRandomAccess = new RandomAccessFile(enterRecordsFile, "r");
            this.exitRecordRandomAccess = new RandomAccessFile(exitRecordsFile, "r");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void persistBatch(
            CallEnterRecordList enterRecords,
            CallExitRecordList exitRecords,
            MethodInfoList methodInfoList,
            List<TClassDescription> classDescriptionList) throws IOException
    {
        Iterator<TMethodInfoDecoder> iterator = methodInfoList.copyingIterator();
        while (iterator.hasNext()) {
            TMethodInfoDecoder methodDescription = iterator.next();
            methodDescriptionMap.put(methodDescription.id(), methodDescription);
        }

        for (TClassDescription classDescription : classDescriptionList) {
            classIdMap.put(
                    classDescription.getId(),
                    new NameOnlyTypeInfo(classDescription.getId(), classDescription.getName())
            );
        }

        long prevEnterRecordPos = enterPos;
        long prevExitRecordPos = exitPos;

        byte[] bytes = enterRecords.toByteString().toByteArray();
        enterRecordsOutputStream.write(bytes);
        enterRecordsOutputStream.flush();
        enterPos += bytes.length;

        bytes = exitRecords.toByteString().toByteArray();
        exitRecordsOutputStream.write(bytes);
        exitRecordsOutputStream.flush();
        exitPos += bytes.length;

        AddressableItemIterator<TCallEnterRecordDecoder> enterRecordIterator = enterRecords.iterator();

        while (enterRecordIterator.hasNext()) {
            long addr = enterRecordIterator.address();
            TCallEnterRecordDecoder enterRecord = enterRecordIterator.next();
            enterRecordPos.put(enterRecord.callId(), prevEnterRecordPos + addr);
        }

        AddressableItemIterator<TCallExitRecordDecoder> exitIterator = exitRecords.iterator();

        while (exitIterator.hasNext()) {
            long addr = exitIterator.address();
            TCallExitRecordDecoder exitRecord = exitIterator.next();
            exitRecordPos.put(exitRecord.callId(), prevExitRecordPos + addr);
        }

        linkChildren(enterRecords, exitRecords);
    }

    private void linkChildren(CallEnterRecordList enterRecords, CallExitRecordList exitRecords) {
        PeekingIterator<TCallEnterRecordDecoder> enterRecordIt = Iterators.peekingIterator(enterRecords.iterator());
        PeekingIterator<TCallExitRecordDecoder> exitRecordIt = Iterators.peekingIterator(exitRecords.iterator());

        if (currentRootStack.isEmpty()) {
            TCallEnterRecordDecoder enterRecord = enterRecordIt.next();
            if (enterRecord.callId() != 0) {
                throw new RuntimeException("Call id of the root must be 0");
            }
            currentRootStack.push(enterRecord.callId());
        }

        while (enterRecordIt.hasNext() || exitRecordIt.hasNext()) {
            long currentCallId = currentRootStack.topLong();

            if (exitRecordIt.hasNext() && exitRecordIt.peek().callId() == currentCallId) {
                exitRecordIt.next();
                currentRootStack.popLong();
            } else if (enterRecordIt.hasNext()) {
                TCallEnterRecordDecoder enterRecord = enterRecordIt.next();
                linkChild(currentCallId, enterRecord.callId());
                currentRootStack.push(enterRecord.callId());
            } else {
                throw new RuntimeException("Inconsistent state");
            }
        }
    }

    private static final int RECORD_HEADER_LENGTH = 2 * Integer.BYTES;

    @Override
    public synchronized CallRecord find(long id) {
        int idd = (int) id;
        long enterRecordAddress = enterRecordPos.get(idd);
        if (enterRecordAddress == -1) {
            return null;
        }
        try {
            enterRecordRandomAccess.seek(enterRecordAddress);

            int bytesRead = enterRecordRandomAccess.read(tmpBuf);
            UnsafeBuffer unsafeBuffer = new UnsafeBuffer(tmpBuf, 0, bytesRead);
            TCallEnterRecordDecoder enterRecordDecoder = new TCallEnterRecordDecoder();
            int blockLength = unsafeBuffer.getInt(Integer.BYTES);
            enterRecordDecoder.wrap(unsafeBuffer, RECORD_HEADER_LENGTH, blockLength, 0);

            List<ObjectRepresentation> args = new ArrayList<>();

            TCallEnterRecordDecoder.ArgumentsDecoder arguments = enterRecordDecoder.arguments();
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
            enterRecordDecoder.wrapCallee(buffer);

            TypeInfo calleeTypeInfo = classIdMap.get(enterRecordDecoder.calleeClassId());

            ObjectRepresentation callee = ObjectBinaryPrinterType.printerForId(enterRecordDecoder.calleePrinterId()).read(
                    calleeTypeInfo,
                    new BinaryInputImpl(buffer),
                    decodingContext
            );
            CallRecord callRecord = new CallRecord(
                    id,
                    callee,
                    args,
                    methodDescriptionMap.get(enterRecordDecoder.methodId()),
                    this,
                    0
            );

            long exitPos = exitRecordPos.get(idd);
            if (exitPos == -1) {
                return callRecord;
            }
            exitRecordRandomAccess.seek(exitPos);
            bytesRead = exitRecordRandomAccess.read(tmpBuf);
            unsafeBuffer = new UnsafeBuffer(tmpBuf, 0, bytesRead);
            TCallExitRecordDecoder exitRecordDecoder = new TCallExitRecordDecoder();
            exitRecordDecoder.wrap(unsafeBuffer, RECORD_HEADER_LENGTH, unsafeBuffer.getInt(Integer.BYTES), 0);

            UnsafeBuffer returnValueBuffer = new UnsafeBuffer();
            exitRecordDecoder.wrapReturnValue(returnValueBuffer);
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(exitRecordDecoder.returnPrinterId());
            ObjectRepresentation returnValue = printer.read(classIdMap.get(exitRecordDecoder.returnClassId()), new BinaryInputImpl(returnValueBuffer), decodingContext);
            boolean thrown = exitRecordDecoder.thrown() == BooleanType.T;

            callRecord.setReturnValue(returnValue);
            callRecord.setThrown(thrown);

            return callRecord;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void deleteSubtree(long id) {
//        for (CallRecord child : getChildren(id)) {
//            deleteSubtree(child.getId());
//        }
//        nodes.remove(id);
    }

    @Override
    public synchronized List<CallRecord> getChildren(long id) {
        return getChildrenIds(id).stream().map(this::find).collect(Collectors.toList());
    }

    @Override
    public synchronized LongList getChildrenIds(long id) {
        return new LongArrayList(children.getOrDefault((int) id, LongLists.EMPTY_LIST));
    }

    @Override
    public synchronized void persist(CallRecord node) {
//        if (node.getId() < 0) {
//            long id = ++idGenerator;
//            node.setId(id);
//        }
//        nodes.put(node.getId(), node);
    }

    @Override
    public synchronized void close() {
        try {
            enterRecordsOutputStream.close();
            exitRecordsOutputStream.close();
            exitRecordRandomAccess.close();
            enterRecordRandomAccess.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void linkChild(long parentId, long childId) {
        children.computeIfAbsent((int) parentId, i -> new LongArrayList()).add(childId);
    }
}
