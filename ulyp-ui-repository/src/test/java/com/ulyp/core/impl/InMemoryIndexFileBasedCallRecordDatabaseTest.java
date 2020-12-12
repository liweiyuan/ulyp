package com.ulyp.core.impl;

import com.ulyp.core.*;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.TClassDescription;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InMemoryIndexFileBasedCallRecordDatabaseTest {

    private final TestAgentRuntime agentRuntime = new TestAgentRuntime();

    @Test
    public void test() throws IOException {
        CallEnterRecordList enterRecords = new CallEnterRecordList();
        CallExitRecordList exitRecords = new CallExitRecordList();
        MethodInfoList methodInfos = new MethodInfoList();
        List<TClassDescription> classDescriptionList = new ArrayList<>();

        MethodInfo toStringMethod = new MethodInfo(
                100,
                "toString",
                false,
                true,
                new ArrayList<>(),
                agentRuntime.get(String.class),
                agentRuntime.get(InMemoryIndexFileBasedCallRecordDatabaseTest.class));

        methodInfos.add(toStringMethod);

        enterRecords.add(
                0,
                100,
                agentRuntime,
                new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance()},
                this,
                new Object[]{}
        );
        enterRecords.add(
                1,
                100,
                agentRuntime,
                new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance()},
                this,
                new Object[]{}
        );
        enterRecords.add(
                2,
                100,
                agentRuntime,
                new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance()},
                this,
                new Object[]{}
        );
        exitRecords.add(2, 100, agentRuntime, false, ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance(), "asdasdad");
        exitRecords.add(1, 100, agentRuntime, false, ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance(), "asdasdad");
        exitRecords.add(0, 100, agentRuntime, false, ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance(), "asdasdad");

        OnDiskFileBasedCallRecordDatabase fileBasedCallRecordDatabase = new OnDiskFileBasedCallRecordDatabase("test");

        fileBasedCallRecordDatabase.persistBatch(enterRecords, exitRecords, methodInfos, classDescriptionList);

        CallRecord root = fileBasedCallRecordDatabase.find(0);

        assertEquals(0, root.getId());
        assertTrue(root.getParameterNames().isEmpty());

        assertEquals(1, root.getChildren().size());
    }

    @Test
    public void testSavingViaChunks() throws IOException {
        CallEnterRecordList enterRecords = new CallEnterRecordList();
        CallExitRecordList exitRecords = new CallExitRecordList();
        MethodInfoList methodInfos = new MethodInfoList();
        List<TClassDescription> classDescriptionList = new ArrayList<>();

        MethodInfo toStringMethod = new MethodInfo(
                100,
                "toString",
                false,
                true,
                new ArrayList<>(),
                agentRuntime.get(String.class),
                agentRuntime.get(InMemoryIndexFileBasedCallRecordDatabaseTest.class));

        methodInfos.add(toStringMethod);

        enterRecords.add(
                0,
                100,
                agentRuntime,
                new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance()},
                this,
                new Object[]{}
        );
        enterRecords.add(
                1,
                100,
                agentRuntime,
                new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance()},
                this,
                new Object[]{}
        );
        enterRecords.add(
                2,
                100,
                agentRuntime,
                new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance()},
                this,
                new Object[]{}
        );
        exitRecords.add(2, 100, agentRuntime, false, ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance(), "asdasdad");

        OnDiskFileBasedCallRecordDatabase database = new OnDiskFileBasedCallRecordDatabase("tmp");

        database.persistBatch(enterRecords, exitRecords, methodInfos, classDescriptionList);

        CallRecord root = database.getRoot();

        assertEquals(0, root.getId());
        assertTrue(root.getParameterNames().isEmpty());

        database.find(1);
        database.find(2);
    }
}