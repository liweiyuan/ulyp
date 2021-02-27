package com.ulyp.database;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class DatabaseReader {

    private final EnhancedRandomAccessFile randomAccessFile;

    public DatabaseReader(Path path) throws IOException {
        this.randomAccessFile = new EnhancedRandomAccessFile(new RandomAccessFile(path.toFile(), "r"));
    }

    public void read() throws IOException {
        System.out.println(randomAccessFile.readInt(0L));
    }
}
