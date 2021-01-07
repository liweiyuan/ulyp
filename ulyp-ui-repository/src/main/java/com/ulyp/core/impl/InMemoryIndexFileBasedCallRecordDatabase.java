package com.ulyp.core.impl;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.ulyp.core.*;
import com.ulyp.core.printers.*;
import com.ulyp.core.printers.bytes.BinaryInputImpl;
import com.ulyp.transport.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import org.agrona.concurrent.UnsafeBuffer;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryIndexFileBasedCallRecordDatabase implements CallRecordDatabase {

    private final File enterRecordsFile;
    private final File exitRecordsFile;

    private final OutputStream enterRecordsOutputStream;
    private final RandomAccessFile enterRecordRandomAccess;
    private final RandomAccessFile exitRecordRandomAccess;
    private final OutputStream exitRecordsOutputStream;

    private boolean open = true;
    private long enterPos = 0;
    private long exitPos = 0;
    private final AtomicLong totalCount = new AtomicLong(0);
    private final Int2ObjectMap<IntList> children = new Int2ObjectOpenHashMap<>();
    private final Int2IntMap idToParentIdMap = new Int2IntOpenHashMap();
    private final Int2IntMap idToSubtreeCountMap = new Int2IntOpenHashMap();
    private final Long2LongMap enterRecordPos = new Long2LongOpenHashMap();
    private final Long2LongMap exitRecordPos = new Long2LongOpenHashMap();
    private final Long2ObjectMap<TypeInfo> classIdMap = new Long2ObjectOpenHashMap<>();
    private final DecodingContext decodingContext = new DecodingContext(classIdMap);
    private final Long2ObjectMap<TMethodInfoDecoder> methodDescriptionMap = new Long2ObjectOpenHashMap<>();
    private final LongArrayList currentRootStack = new LongArrayList();
    private final byte[] tmpBuf = new byte[512 * 1024];

    public InMemoryIndexFileBasedCallRecordDatabase() {
        this("");
    }

    public InMemoryIndexFileBasedCallRecordDatabase(String name) {
        exitRecordPos.defaultReturnValue(-1L);
        enterRecordPos.defaultReturnValue(-1L);
        idToParentIdMap.defaultReturnValue(-1);
        idToSubtreeCountMap.defaultReturnValue(-1);

        try {
            enterRecordsFile = File.createTempFile("ulyp-" + name + "-enter-records", null);
            enterRecordsFile.deleteOnExit();
            exitRecordsFile = File.createTempFile("ulyp-" + name + "-exit-records", null);
            exitRecordsFile.deleteOnExit();

            this.enterRecordsOutputStream = new BufferedOutputStream(new FileOutputStream(enterRecordsFile, false));
            this.exitRecordsOutputStream = new BufferedOutputStream(new FileOutputStream(exitRecordsFile, false));
            this.enterRecordRandomAccess = new RandomAccessFile(enterRecordsFile, "r");
            this.exitRecordRandomAccess = new RandomAccessFile(exitRecordsFile, "r");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void persistBatch(
            CallEnterRecordList enterRecords,
            CallExitRecordList exitRecords,
            MethodInfoList methodInfoList,
            List<TClassDescription> classDescriptionList) throws IOException
    {
        checkOpen();

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

        updateChildrenParentAndSubtreeCountMaps(enterRecords, exitRecords);
    }

    private synchronized void updateChildrenParentAndSubtreeCountMaps(CallEnterRecordList enterRecords, CallExitRecordList exitRecords) {
        checkOpen();

        PeekingIterator<TCallEnterRecordDecoder> enterRecordIt = Iterators.peekingIterator(enterRecords.iterator());
        PeekingIterator<TCallExitRecordDecoder> exitRecordIt = Iterators.peekingIterator(exitRecords.iterator());

        if (currentRootStack.isEmpty()) {
            TCallEnterRecordDecoder enterRecord = enterRecordIt.next();
            if (enterRecord.callId() != 0) {
                throw new RuntimeException("Call id of the root must be 0");
            }
            currentRootStack.push(enterRecord.callId());
            idToSubtreeCountMap.put((int) enterRecord.callId(), 1);
        }

        while (enterRecordIt.hasNext() || exitRecordIt.hasNext()) {
            long currentCallId = currentRootStack.topLong();

            if (exitRecordIt.hasNext() && exitRecordIt.peek().callId() == currentCallId) {
                exitRecordIt.next();
                currentRootStack.popLong();
            } else if (enterRecordIt.hasNext()) {
                TCallEnterRecordDecoder enterRecord = enterRecordIt.next();

                idToParentIdMap.put((int) enterRecord.callId(), (int) currentCallId);
                children.computeIfAbsent((int) currentCallId, i -> new IntArrayList()).add((int) enterRecord.callId());
                idToSubtreeCountMap.put((int) enterRecord.callId(), 1);

                for (int i = 0; i < currentRootStack.size(); i++) {
                    int id = (int) currentRootStack.getLong(i);
                    idToSubtreeCountMap.put(id, idToSubtreeCountMap.get(id) + 1);
                }

                currentRootStack.push(enterRecord.callId());
                totalCount.lazySet(totalCount.get() + 1);
            } else {
                if (!currentRootStack.isEmpty() && currentRootStack.size() > 1) {
                    currentRootStack.popLong();
                } else {
                    throw new RuntimeException("Inconsistent state");
                }
            }
        }
    }

    private static final int RECORD_HEADER_LENGTH = 2 * Integer.BYTES;

    @Override
    public synchronized CallRecord find(long id) {
        checkOpen();

        long enterRecordAddress = enterRecordPos.get(id);
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
                    this
            );

            long exitPos = exitRecordPos.get(id);
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

    public synchronized void deleteSubtree(long id) {
        checkOpen();

//        for (CallRecord child : getChildren(id)) {
//            deleteSubtree(child.getId());
//        }
//        nodes.remove(id);
        // TODO implement
    }

    @Override
    public synchronized LongList getChildrenIds(long id) {
        IntList childrenIds = children.getOrDefault((int) id, IntLists.EMPTY_LIST);
        LongArrayList longs = new LongArrayList();
        for (int i = 0; i < childrenIds.size(); i++) {
            longs.add(childrenIds.getInt(i));
        }
        return longs;
    }

    @Override
    public long countAll() {
        return totalCount.get();
    }

    @Override
    public long getSubtreeCount(long id) {
        return idToSubtreeCountMap.get((int) id);
    }

    private synchronized void checkOpen() {
        if (!open) {
            throw new IllegalStateException("Database is closed");
        }
    }

    @Override
    public synchronized void close() {
        try {
            checkOpen();

            enterRecordsOutputStream.close();
            exitRecordsOutputStream.close();
            exitRecordRandomAccess.close();
            enterRecordRandomAccess.close();

            enterRecordsFile.delete();
            exitRecordsFile.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            open = false;
        }
    }
}
