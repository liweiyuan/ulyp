package com.ulyp.core;

public class TracingContext {

    private final MethodDescriptionDictionary methodDescriptionDictionary;

    public TracingContext(MethodDescriptionDictionary methodDescriptionDictionary) {
        this.methodDescriptionDictionary = methodDescriptionDictionary;
    }

    public long getClassId(Object o) {
        return o != null ? methodDescriptionDictionary.get(o.getClass()).getId() : -1;
    }
}
