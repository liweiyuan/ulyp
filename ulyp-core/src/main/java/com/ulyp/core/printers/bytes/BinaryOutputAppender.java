package com.ulyp.core.printers.bytes;

import org.agrona.concurrent.UnsafeBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    public void append(int value) {
        tmpBuffer.putInt(bytePos, value);
        bytePos += Integer.BYTES;
    }

    public void append(String value) {
        if (value != null) {
            // TODO optimize
            byte[] bytes = value.getBytes(UTF_8);
            append(bytes.length);
            tmpBuffer.putBytes(bytePos, bytes);
            bytePos += bytes.length;
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
    public void write(int val) throws Exception {
        append(val);
    }

    @Override
    public void write(String value) throws Exception {
        append(value);
    }
}
