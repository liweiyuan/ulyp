package com.ulyp.core.printers.bytes;

public interface BinaryInput {

    boolean readBoolean();

    byte readByte();

    int readInt();

    long readLong();

    default String readString() {
        StringView view = readStringView();
        if (view != null) {
            return view.toString();
        } else {
            return null;
        }
    }

    StringView readStringView();
}
