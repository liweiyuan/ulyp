package com.ulyp.core.printers.bytes;

import org.agrona.concurrent.UnsafeBuffer;

import java.nio.charset.StandardCharsets;

public class BinaryOutputAppender implements AutoCloseable, BinaryOutput {

    private final byte[] tmp = new byte[32 * 1024];
    private final UnsafeBuffer tmpBuffer = new UnsafeBuffer(tmp);

    private final AbstractBinaryOutput binaryOutput;
    private int bytePos = 0;
    private int refCount = 0;

    public BinaryOutputAppender(AbstractBinaryOutput binaryOutput) {
        this.binaryOutput = binaryOutput;
    }

    public void reset() {
        bytePos = 0;
        refCount = 1;
    }

    public void append(boolean value) {
        append(value ? 1 : 0);
    }

    public void append(int value) {
        tmpBuffer.putInt(bytePos, value);
        bytePos += Integer.BYTES;
    }

    public void append(long value) {
        tmpBuffer.putLong(bytePos, value);
        bytePos += Long.BYTES;
    }

    public void append(byte c) {
        tmpBuffer.putByte(bytePos, c);
        bytePos += Byte.BYTES;
    }

    public void append(String value) {
        if (value != null) {
            // TODO optimize for ASCII only strings
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            append(bytes.length);
            for (byte b : bytes) {
                append(b);
            }
        } else {
            append(-1);
        }
    }

    @Override
    public void close() throws Exception {
        refCount--;
        if (refCount == 0) {
            binaryOutput.write(tmpBuffer, bytePos);
        }
    }

    @Override
    public BinaryOutputAppender appender() {
        refCount++;
        return this;
    }

    @Override
    public void writeBool(boolean val) throws Exception {
        append(val);
    }

    @Override
    public void writeInt(int val) throws Exception {
        append(val);
    }

    @Override
    public void writeLong(long val) throws Exception {
        append(val);
    }

    @Override
    public void writeString(String value) throws Exception {
        append(value);
    }
}
