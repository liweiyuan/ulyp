package com.ulyp.core;

public class ClassDescription {

    public static final ClassDescription UNKNOWN_CLASS_DESCRIPTION = new ClassDescription(-1, "UNKNOWN", "UNKNOWN");

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
