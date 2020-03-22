package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.transport.TMethodEnterTraceDecoder;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class MethodEnterTraceListTest {

    @Test
    public void test() {
        MethodEnterTraceList list = new MethodEnterTraceList();

        Object obj = new Object();

        list.add(5344, 231, new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter()}, new Object[] { obj });
        list.add(65345, 2341, new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter() }, new Object[] {"zasda"});
        list.add(7, 3, new ObjectBinaryPrinter[] {
                ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter(),
                ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter()},
                new Object[] {"cvxzxda", obj});

        assertEquals(3, list.size());

        Iterator<TMethodEnterTraceDecoder> it = list.iterator();

        assertTrue(it.hasNext());

        TMethodEnterTraceDecoder t0 = it.next();

        assertEquals(5344, t0.callId());
        assertEquals(231, t0.methodId());
        TMethodEnterTraceDecoder.ArgumentsDecoder t0args = t0.arguments();
        assertTrue(t0args.hasNext());
        assertEquals("Object@" + System.identityHashCode(obj), t0args.next().value());
        assertFalse(t0args.hasNext());

        assertTrue(it.hasNext());

        TMethodEnterTraceDecoder t1 = it.next();
        assertEquals(65345, t1.callId());
        assertEquals(2341, t1.methodId());
        TMethodEnterTraceDecoder.ArgumentsDecoder t1args = t1.arguments();
        assertTrue(t1args.hasNext());
        assertEquals("zasda", t1args.next().value());
        assertFalse(t1args.hasNext());

        assertTrue(it.hasNext());

        TMethodEnterTraceDecoder t2 = it.next();
        assertEquals(7, t2.callId());
        assertEquals(3, t2.methodId());
        TMethodEnterTraceDecoder.ArgumentsDecoder t2args = t2.arguments();
        assertTrue(t2args.hasNext());
        assertEquals("cvxzxda", t2args.next().value());
        assertEquals("Object@" + System.identityHashCode(obj), t2args.next().value());
        assertFalse(t2args.hasNext());

        assertFalse(it.hasNext());
    }

    @Test
    public void testCopyingIterator() {
        MethodEnterTraceList list = new MethodEnterTraceList();

        Object obj = new Object();

        list.add(5344, 231, new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter()}, new Object[] { obj });
        list.add(65345, 2341, new ObjectBinaryPrinter[] {ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter() }, new Object[] {"zasda"});
        list.add(7, 3, new ObjectBinaryPrinter[] {
                        ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter(),
                        ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter()},
                new Object[] {"cvxzxda", obj});

        List<TMethodEnterTraceDecoder> output = new ArrayList<>();
        list.copyingIterator().forEachRemaining(output::add);

        assertThat(output, Matchers.hasSize(3));

        TMethodEnterTraceDecoder t2 = output.get(2);
        assertEquals(7, t2.callId());
        assertEquals(3, t2.methodId());
        TMethodEnterTraceDecoder.ArgumentsDecoder t2args = t2.arguments();
        assertTrue(t2args.hasNext());
        assertEquals("cvxzxda", t2args.next().value());
        assertEquals("Object@" + System.identityHashCode(obj), t2args.next().value());
    }
}