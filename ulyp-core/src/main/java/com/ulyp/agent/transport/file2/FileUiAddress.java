package com.ulyp.agent.transport.file2;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UiTransport;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUiAddress implements UiAddress {

    private final Path filePath;

    public FileUiAddress(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    @Override
    public UiTransport buildTransport() throws IOException {
        return new FileUiTransport(filePath);
    }
}
