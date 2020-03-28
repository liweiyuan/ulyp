package com.ulyp.core;

public class ClassDescription {

    private final long id;
    private final String name;

    public ClassDescription(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
