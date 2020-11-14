package com.ulyp.core.printers.bytes;

import org.agrona.DirectBuffer;

import java.nio.charset.StandardCharsets;

public class BinaryInputImpl implements BinaryInput {

    private final DirectBuffer buffer;
    private int bytePos = 0;

    public BinaryInputImpl(DirectBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public boolean readBoolean() {
        long val = readInt();
        return val == 1;
    }

    @Override
    public byte readByte() {
        byte val = buffer.getByte(bytePos);
        bytePos += Byte.BYTES;
        return val;
    }

    @Override
    public int readInt() {
        int val = buffer.getInt(bytePos);
        bytePos += Integer.BYTES;
        return val;
    }

    @Override
    public long readLong() {
        long val = buffer.getLong(bytePos);
        bytePos += Long.BYTES;
        return val;
    }

    @Override
    public String readString() {
        int length = readInt();
        if (length >= 0) {
            byte[] buf = new byte[length];
            this.buffer.getBytes(bytePos, buf);
            bytePos += length;
            return new String(buf, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}
