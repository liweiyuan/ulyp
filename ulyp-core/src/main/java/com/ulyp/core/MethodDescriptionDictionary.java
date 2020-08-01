package com.ulyp.core;

import com.ulyp.core.util.ClassUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MethodDescriptionDictionary {

    private static final MethodDescriptionDictionary INSTANCE = new MethodDescriptionDictionary();

    public static MethodDescriptionDictionary getInstance() {
        return INSTANCE;
    }

    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, MethodDescription> methodMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, ClassDescription> classDescriptionMap = new ConcurrentHashMap<>();

    private MethodDescriptionDictionary() {
    }

    public MethodDescription get(long id) {
        return methodMap.get(id);
    }

    public ClassDescription get(Class<?> clazz) {
        return classDescriptionMap.computeIfAbsent(
                clazz,
                cl -> new ClassDescription(idGenerator.incrementAndGet(), ClassUtils.getSimpleNameSafe(cl), cl.getName())
        );
    }

    public Collection<MethodDescription> getMethodDescriptions() {
        return methodMap.values();
    }

    public Collection<ClassDescription> getClassDescriptions() {
        return classDescriptionMap.values();
    }

    public void put(long id, MethodDescription buildAs) {
        methodMap.put(id, buildAs);
    }
}
