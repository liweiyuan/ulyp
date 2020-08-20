package com.ulyp.core.printers.bytes;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BinaryInputOutputTest {

    private final UnsafeBuffer buffer = new UnsafeBuffer(new byte[16 * 1024]);

    private final BinaryOutput binaryOutput = new AbstractBinaryOutput() {

        @Override
        public void write(byte[] bytes) {

        }

        @Override
        public void write(UnsafeBuffer unsafeBuffer, int length) {
            buffer.putBytes(0, unsafeBuffer, 0, length);
        }
    };

    private final BinaryInput binaryInput = new BinaryInputImpl(buffer);

    @Test
    public void testSimpleReadWrite() throws Exception {
        try (BinaryOutputAppender appender = binaryOutput.appender()) {
            appender.append("abc");
        }

        assertEquals("abc", binaryInput.readString());
    }

    @Test
    public void test() throws Exception {
        try (BinaryOutputAppender appender = binaryOutput.appender()) {
            appender.append(2);
            appender.append(null);
            appender.append(6);
        }

        assertEquals(2, binaryInput.readLong());
        assertNull(binaryInput.readString());
        assertEquals(6, binaryInput.readLong());
    }

    @Test
    public void testNestedAppender() throws Exception {
        try (BinaryOutputAppender appender = binaryOutput.appender()) {
            appender.append(2);
            try (BinaryOutputAppender appender2 = appender.appender()) {
                appender2.append("asdas");
            }
            appender.append(6);
            appender.append(null);
        }

        assertEquals(2, binaryInput.readLong());
        assertEquals("asdas", binaryInput.readString());
        assertEquals(6, binaryInput.readLong());
        assertNull(binaryInput.readString());
    }
}
