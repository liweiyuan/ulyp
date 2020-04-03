package com.ulyp.core.printers.bytes;

public interface BinaryOutput {

    BinaryOutputAppender appender();

    void write(int val) throws Exception;

    void write(final String value) throws Exception;
}
