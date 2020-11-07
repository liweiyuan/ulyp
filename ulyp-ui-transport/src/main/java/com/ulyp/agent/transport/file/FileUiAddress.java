package com.ulyp.agent.transport.file;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.transport.Settings;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUiAddress implements UiAddress {

    private final Settings settings;
    private final Path filePath;

    public FileUiAddress(Settings settings, String filePath) {
        this.filePath = Paths.get(filePath);
        this.settings = settings;
    }

    @Override
    public UiTransport buildTransport() {
        return new FileUiTransport(settings, filePath);
    }
}
