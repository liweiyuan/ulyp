package com.ulyp.core;

import com.ulyp.core.impl.HeapCallRecordDatabase;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.TClassDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CallRecordTreeDeserializerTest {

    private TestAgentRuntime agentRuntime = new TestAgentRuntime();

    @Test
    public void testSingleCall() {
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
                agentRuntime.get(CallRecordTreeDeserializerTest.class));

        methodInfos.add(toStringMethod);

        enterRecords.add(
                0,
                100,
                agentRuntime,
                new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance()},
                this,
                new Object[]{}
        );

        exitRecords.add(0, 100, agentRuntime, false, ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance(), "asdasdad");

        CallRecordTreeDeserializer callRecordTreeDeserializer = new CallRecordTreeDeserializer(enterRecords, exitRecords, methodInfos, classDescriptionList, new HeapCallRecordDatabase());

        System.out.println(callRecordTreeDeserializer.get());
    }

    @Test
    public void testThreeCalls() {
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
                agentRuntime.get(CallRecordTreeDeserializerTest.class));

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

        CallRecordTreeDeserializer callRecordTreeDeserializer = new CallRecordTreeDeserializer(enterRecords, exitRecords, methodInfos, classDescriptionList, new HeapCallRecordDatabase());

        CallRecord rootCall = callRecordTreeDeserializer.get();

        assertThat(rootCall.getChildren(), Matchers.hasSize(1));

        assertThat(rootCall.getChildren().get(0).getChildren(), Matchers.hasSize(1));
    }
}