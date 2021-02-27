package com.ulyp.database;

import java.io.IOException;
import java.io.OutputStream;

public class WithSizeOutputStream extends OutputStream {

    private final OutputStream delegate;
    private long byteSize;

    public WithSizeOutputStream(OutputStream delegate) {
        this.delegate = delegate;
        this.byteSize = 0;
    }

    public long getSize() {
        return byteSize;
    }

    @Override
    public void write(int b) throws IOException {
        this.delegate.write(b);
        byteSize++;
    }
}
