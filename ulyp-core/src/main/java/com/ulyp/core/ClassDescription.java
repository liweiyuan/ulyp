package com.ulyp.core;

public class ClassDescription {

    private final long id;
    private final String simpleName;
    private final String name;

    public ClassDescription(long id, String simpleName, String name) {
        this.id = id;
        this.simpleName = simpleName;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getName() {
        return name;
    }
}
