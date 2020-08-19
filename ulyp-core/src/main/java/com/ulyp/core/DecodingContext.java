package com.ulyp.core;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class DecodingContext {

    private final Long2ObjectMap<ClassDescription> classIdMap;

    public DecodingContext(Long2ObjectMap<ClassDescription> classIdMap) {
        this.classIdMap = classIdMap;
    }

    public ClassDescription getClass(long id) {
        return classIdMap.getOrDefault(id, ClassDescription.UNKNOWN);
    }
}
