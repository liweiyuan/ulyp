package com.ulyp.core.printers.bytes;

public interface BinaryInput {

    boolean readBoolean();

    byte readByte();

    int readInt();

    long readLong();

    String readString();
}
