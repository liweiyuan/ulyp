package com.ulyp.core;

public class ClassDescription {

    public static final ClassDescription UNKNOWN_CLASS_DESCRIPTION = new ClassDescription(-1, "UNKNOWN", "UNKNOWN");

    private final long id;
    private final String simpleName;
    private final String name;

    public ClassDescription(long id, String simpleName, String name) {
        this.id = id;
        this.simpleName = simpleName.intern();
        this.name = name.intern();
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

    @Override
    public String toString() {
        return simpleName;
    }
}
