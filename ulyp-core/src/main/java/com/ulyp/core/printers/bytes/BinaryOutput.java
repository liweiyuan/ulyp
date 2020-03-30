package com.ulyp.core.printers.bytes;

import org.agrona.concurrent.UnsafeBuffer;

import java.io.UnsupportedEncodingException;

public abstract class BinaryOutput {

    private final byte[] tmp = new byte[10 * 1024];
    private final UnsafeBuffer tmpBuffer = new UnsafeBuffer(tmp);

    public void write(int val) {
        tmpBuffer.putInt(0, val);
        write(tmpBuffer, Integer.BYTES);
    }

    public void write(final String value) {
        byte[] bytes;
        try {
            bytes = null == value || value.isEmpty() ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        write(bytes);
    }

    public abstract void write(final byte[] bytes);

    public abstract void write(final UnsafeBuffer unsafeBuffer, int length);
}
