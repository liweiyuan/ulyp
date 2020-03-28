package com.ulyp.agent;

import com.ulyp.agent.util.Log;
import com.ulyp.core.ClassDescription;
import com.ulyp.core.MethodDescription;

import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MethodDescriptionDictionary {

    private final Log log;
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Executable, MethodDescription> methodMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, ClassDescription> classDescriptionMap = new ConcurrentHashMap<>();

    public MethodDescriptionDictionary(Log log) {
        this.log = log;
    }

    public MethodDescription get(Executable exec) {
        return methodMap.computeIfAbsent(exec, method -> {
            long id = idGenerator.incrementAndGet();
            log.log(() -> "Method " + exec + " mapped to id " + id);
            return new MethodDescription(id, get(method.getDeclaringClass()), method);
        });
    }

    private ClassDescription get(Class<?> clazz) {
        return classDescriptionMap.computeIfAbsent(clazz, cl -> new ClassDescription(idGenerator.incrementAndGet(), cl.getName()));
    }

    Collection<MethodDescription> getMethodInfos() {
        return methodMap.values();
    }
}
