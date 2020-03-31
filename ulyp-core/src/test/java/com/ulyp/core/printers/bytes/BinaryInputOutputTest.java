package com.ulyp.core.printers.bytes;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
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
    public void test() throws Exception {
        try (BinaryOutputAppender appender = binaryOutput.appender()) {
            appender.append(2);
            appender.append(null);
            appender.append(6);
        }

        assertEquals(2, binaryInput.readInt());
        assertNull(binaryInput.readString());
        assertEquals(6, binaryInput.readInt());
    }

    @Test
    public void testNestedAppender() throws Exception {
        try (BinaryOutputAppender appender = binaryOutput.appender()) {
            appender.append(2);
            try (BinaryOutputAppender appender2 = appender.appender()) {
                appender2.append("asdas");
            }
            appender.append(6);
        }

        assertEquals(2, binaryInput.readInt());
        assertEquals("asdas", binaryInput.readString());
        assertEquals(6, binaryInput.readInt());
    }
}
