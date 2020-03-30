package com.ulyp.core.printers.bytes;

import org.agrona.DirectBuffer;

public class BinaryInputImpl implements BinaryInput {

    private final DirectBuffer buffer;
    private int pos = 0;

    public BinaryInputImpl(DirectBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int readInt() {
        return buffer.getInt(pos);
    }

    @Override
    public String readString() {
        byte[] b = new byte[buffer.capacity()];
        buffer.getBytes(0, b);
        return new String(b);
    }
}
