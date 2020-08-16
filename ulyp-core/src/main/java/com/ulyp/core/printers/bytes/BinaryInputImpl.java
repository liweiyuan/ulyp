package com.ulyp.core.printers.bytes;

import org.agrona.DirectBuffer;

public class BinaryInputImpl implements BinaryInput {

    private final DirectBuffer buffer;
    private int bytePos = 0;

    public BinaryInputImpl(DirectBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public boolean readBoolean() {
        long val = readLong();
        return val == 1;
    }

    @Override
    public long readLong() {
        long val = buffer.getLong(bytePos);
        bytePos += Long.BYTES;
        return val;
    }

    @Override
    public StringView readString() {
        long length = readLong();
        if (length >= 0) {
            StringView view = new StringView();
            view.wrap(buffer, bytePos, (int) length);
            bytePos += length;
            return view;
        } else {
            return null;
        }
    }
}
