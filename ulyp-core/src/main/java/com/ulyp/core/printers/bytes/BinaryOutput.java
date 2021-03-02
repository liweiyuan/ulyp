package com.ulyp.core.printers.bytes;

public interface BinaryOutput {

    BinaryOutputAppender appender();

    Checkpoint checkpoint();

    void writeBool(boolean val) throws Exception;

    void writeInt(int val) throws Exception;

    void writeLong(long val) throws Exception;

    void writeString(final String value) throws Exception;
}
