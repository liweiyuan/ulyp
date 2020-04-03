package com.ulyp.core;

import com.ulyp.core.util.ClassUtils;

import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MethodDescriptionDictionary {

    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Executable, MethodDescription> methodMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, ClassDescription> classDescriptionMap = new ConcurrentHashMap<>();

    public MethodDescriptionDictionary() {
    }

    public MethodDescription get(Executable exec) {
        return methodMap.computeIfAbsent(exec, method -> new MethodDescription(idGenerator.incrementAndGet(), get(method.getDeclaringClass()), method));
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
}
