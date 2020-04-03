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

    public char readChar() {
        char val = buffer.getChar(bytePos);
        bytePos += Character.BYTES;
        return val;
    }

    @Override
    public String readString() {
        int length = readInt();
        if (length >= 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(readChar());
            }
            return builder.toString();
        } else {
            return null;
        }
    }
}
