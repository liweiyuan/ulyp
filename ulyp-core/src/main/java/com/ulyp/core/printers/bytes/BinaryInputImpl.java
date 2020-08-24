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
        // TODO maybe optimize
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
    public StringView readStringView() {
        int length = readInt();
        if (length >= 0) {
            StringView view = new StringView();
            view.wrap(buffer, bytePos,  length);
            bytePos += length;
            return view;
        } else {
            return null;
        }
    }
}
