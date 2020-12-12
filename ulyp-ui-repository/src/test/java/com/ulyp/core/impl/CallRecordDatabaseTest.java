package com.ulyp.core.impl;

import com.ulyp.core.*;
import com.ulyp.core.printers.IdentityObjectRepresentation;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.transport.TClassDescription;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

public abstract class CallRecordDatabaseTest {

    protected abstract CallRecordDatabase build();

    private final TestAgentRuntime agentRuntime = new TestAgentRuntime();

    @Test
    public void test() throws IOException {
        CallEnterRecordList enterRecords = new CallEnterRecordList();
        CallExitRecordList exitRecords = new CallExitRecordList();
        MethodInfoList methodInfos = new MethodInfoList();

        MethodInfo toStringMethod = new MethodInfo(
                100,
                "toString",
                false,
                true,
                new ArrayList<>(),
                agentRuntime.get(String.class),
                agentRuntime.get(OnDiskFileBasedCallRecordDatabaseTest.class)
        );

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

        CallRecordDatabase database = build();

        database.persistBatch(enterRecords, exitRecords, methodInfos, Collections.emptyList());

        CallRecord root = database.find(0);

        assertEquals(0, root.getId());
        assertTrue(root.getParameterNames().isEmpty());
        assertThat(root.getMethodName(), Matchers.is("toString"));

        ObjectRepresentation returnValue = root.getReturnValue();
        assertThat(returnValue, Matchers.instanceOf(IdentityObjectRepresentation.class));

        assertEquals(1, root.getChildren().size());
    }

    @Test
    public void testSavingPartialChunk() throws IOException {
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
                agentRuntime.get(OnDiskFileBasedCallRecordDatabaseTest.class));

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

        CallRecordDatabase database = build();

        database.persistBatch(enterRecords, exitRecords, methodInfos, classDescriptionList);

        CallRecord root = database.getRoot();

        assertEquals(0, root.getId());
        assertTrue(root.getParameterNames().isEmpty());
        assertFalse(root.isComplete());

        assertThat(database.find(1), Matchers.notNullValue());
        assertThat(database.find(2), Matchers.notNullValue());
    }
}
