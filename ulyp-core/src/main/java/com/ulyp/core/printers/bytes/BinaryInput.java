package com.ulyp.core.printers.bytes;

public interface BinaryInput {

    boolean readBoolean();

    long readLong();

    StringView readString();
}
