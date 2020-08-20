package com.ulyp.core.printers.bytes;

public interface BinaryInput {

    boolean readBoolean();

    long readLong();

    default String readString() {
        return readStringView().toString();
    }

    StringView readStringView();
}
