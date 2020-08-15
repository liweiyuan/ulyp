package com.ulyp.core.process;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Classpath {

    private final List<String> value;

    public Classpath() {
        this.value = Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator));
    }

    public List<String> toList() {
        return value;
    }
}
