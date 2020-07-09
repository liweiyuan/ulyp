package com.ulyp.core.printers.bytes;

import com.ulyp.core.printers.Printable;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.charset.StandardCharsets;

public class StringView implements Printable {

    private DirectBuffer buffer;
    private int offset;
    private int length;
    private int hashCode;

    public StringView() {

    }

    public StringView(String text) {
        this(new UnsafeBuffer(text.getBytes(StandardCharsets.US_ASCII)));
    }

    public StringView(DirectBuffer buffer) {
        this.buffer = buffer;
        this.offset = 0;
        this.length = buffer.capacity();
    }

    public StringView(DirectBuffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    public void wrap(DirectBuffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    public void putBytesTo(UnsafeBuffer buffer, int offset) {
        buffer.putBytes(offset, this.buffer, offset, length);
    }

    public int length() {
        return length;
    }

    public boolean isNull() {
        return length < 0;
    }

    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        } else {
            int start = offset, end = offset + length;
            int hc = 0;
            for (int i = start; i < end; i++) {
                hc = 31 * hc + this.buffer.getByte(i);
            }
            return hashCode = hc;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() == StringView.class) {
            StringView that = (StringView) o;

            if (this.length() != that.length()) {
                return false;
            }

            int end = length();
            // if both are `null`, then loop is not executed
            for (int i = 0; i < end; i++) {
                // TODO check compilation?
                if (this.buffer.getByte(this.offset + i) != that.buffer.getByte(that.offset + i)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (length >= 0) {
            StringBuilder builder = new StringBuilder();
            final int upTo = offset + length;
            for (int i = offset; i < upTo; i++) {
                builder.append((char) buffer.getByte(i));
            }
            return builder.toString();
        } else {
            return null;
        }
    }

    @Override
    public String print() {
        return toString();
    }
}