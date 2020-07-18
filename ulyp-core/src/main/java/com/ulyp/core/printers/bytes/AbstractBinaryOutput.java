package com.ulyp.core.printers.bytes;

import org.agrona.concurrent.UnsafeBuffer;

public abstract class AbstractBinaryOutput implements BinaryOutput {

    private final BinaryOutputAppender appender = new BinaryOutputAppender(this);

    public BinaryOutputAppender appender() {
        appender.reset();
        return appender;
    }

    public void write(boolean val) throws Exception {
        try (BinaryOutputAppender appender = appender()) {
            appender.append(val);
        }
    }

    public void write(long val) throws Exception {
        try (BinaryOutputAppender appender = appender()) {
            appender.append(val);
        }
    }

    public void write(final String value) throws Exception {
        try (BinaryOutputAppender appender = appender()) {
            appender.append(value);
        }
    }

    public abstract void write(final byte[] bytes);

    public abstract void write(final UnsafeBuffer unsafeBuffer, int length);
}