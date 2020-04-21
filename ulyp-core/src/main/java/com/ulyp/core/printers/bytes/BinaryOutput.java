package com.ulyp.core.printers.bytes;

public interface BinaryOutput {

    BinaryOutputAppender appender();

    void write(boolean val) throws Exception;

    void write(long val) throws Exception;

    void write(final String value) throws Exception;
}
