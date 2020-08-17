package com.ulyp.core.process;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Classpath {

    private final List<String> value;

    public Classpath() {
        this.value = Arrays.asList(
                Pattern.compile(System.getProperty("path.separator"), Pattern.LITERAL).split(System.getProperty("java.class.path"))
        );
    }

    public List<String> toList() {
        return value;
    }
}
