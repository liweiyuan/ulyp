package com.ulyp.core.printers.bytes;

import org.agrona.DirectBuffer;

public class BinaryInputImpl implements BinaryInput {

    private final DirectBuffer buffer;
    private int bytePos = 0;

    public BinaryInputImpl(DirectBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int readInt() {
        int val = buffer.getInt(bytePos);
        bytePos += Integer.BYTES;
        return val;
    }

    @Override
    public String readString() {
        int length = readInt();
        if (length > 0) {
            byte[] bytes = new byte[length];
            buffer.getBytes(bytePos, bytes);
            bytePos += length;
            return new String(bytes);
        } else {
            return null;
        }
    }
}
