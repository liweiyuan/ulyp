package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodExitTraceDecoder;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class MethodExitTraceListTest {

    @Test
    public void test() {
        Object obj = new Object();

        MethodExitTraceList list = new MethodExitTraceList();

        list.add(1, 6, true, ObjectBinaryPrinterType.STRING_PRINTER.getPrinter(), "sdfsdfsdfsdf");
        list.add(2321, 6545, false, ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter(), obj);

        assertEquals(2, list.size());

        Iterator<TMethodExitTraceDecoder> it = list.iterator();
        assertTrue(it.hasNext());

        TMethodExitTraceDecoder decoder = it.next();

        assertEquals(1, decoder.callId());
        assertEquals(6, decoder.methodId());
        assertEquals(BooleanType.T, decoder.thrown());
        assertEquals("'sdfsdfsdfsdf'", decoder.returnValue());

        assertTrue(it.hasNext());

        decoder = it.next();

        assertEquals(2321, decoder.callId());
        assertEquals(6545, decoder.methodId());
        assertEquals(BooleanType.F, decoder.thrown());
        assertEquals("Object@" + System.identityHashCode(obj), decoder.returnValue());
    }
}