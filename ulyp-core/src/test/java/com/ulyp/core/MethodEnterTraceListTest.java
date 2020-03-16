package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.SMethodEnterTraceDecoder;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class MethodEnterTraceListTest {

    @Test
    public void test() {
        MethodEnterTraceList list = new MethodEnterTraceList();

        Object obj = new Object();

        list.add(5344, 231, new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY.getPrinter()}, new Object[] { obj });
        list.add(65345, 2341, new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter() }, new Object[] {"zasda"});
        list.add(7, 3, new ObjectBinaryPrinter[] {
                ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter(),
                ObjectBinaryPrinterType.IDENTITY.getPrinter()},
                new Object[] {"cvxzxda", obj});

        assertEquals(3, list.size());

        Iterator<SMethodEnterTraceDecoder> it = list.iterator();

        assertTrue(it.hasNext());

        SMethodEnterTraceDecoder t0 = it.next();

        assertEquals(5344, t0.callId());
        assertEquals(231, t0.methodId());
        SMethodEnterTraceDecoder.ArgumentsDecoder t0args = t0.arguments();
        assertTrue(t0args.hasNext());
        assertEquals("Object@" + System.identityHashCode(obj), t0args.next().value());
        assertFalse(t0args.hasNext());

        assertTrue(it.hasNext());

        SMethodEnterTraceDecoder t1 = it.next();
        assertEquals(65345, t1.callId());
        assertEquals(2341, t1.methodId());
        SMethodEnterTraceDecoder.ArgumentsDecoder t1args = t1.arguments();
        assertTrue(t1args.hasNext());
        assertEquals("zasda", t1args.next().value());
        assertFalse(t1args.hasNext());

        assertTrue(it.hasNext());

        SMethodEnterTraceDecoder t2 = it.next();
        assertEquals(7, t2.callId());
        assertEquals(3, t2.methodId());
        SMethodEnterTraceDecoder.ArgumentsDecoder t2args = t2.arguments();
        assertTrue(t2args.hasNext());
        assertEquals("cvxzxda", t2args.next().value());
        assertEquals("Object@" + System.identityHashCode(obj), t2args.next().value());
        assertFalse(t2args.hasNext());

        assertFalse(it.hasNext());
    }
}