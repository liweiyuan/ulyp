package com.ulyp.database;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;

public class EnhancedRandomAccessFile implements Closeable {

    private final RandomAccessFile randomAccessFile;

    public EnhancedRandomAccessFile(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }

    public byte[] readBytes(long pos, int length) throws IOException {
        byte[] result = new byte[length];
        int index = 0;

        while (index < length) {
            byte[] tmp = new byte[8 * 1024];
            randomAccessFile.seek(pos);
            int read = randomAccessFile.read(tmp);
            for (int i = 0; i < read && index < length; i++) {
                result[index++] = tmp[i];
            }
        }

        return result;
    }

    public int readInt(long pos) throws IOException {
        randomAccessFile.seek(pos);
        return randomAccessFile.readInt();
    }

    public void writeIntAt(long pos, int value) throws IOException {
        randomAccessFile.seek(pos);
        randomAccessFile.write(value);
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
