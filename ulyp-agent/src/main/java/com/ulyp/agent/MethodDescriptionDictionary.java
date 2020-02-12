package com.ulyp.agent;

import com.ulyp.agent.util.Log;

import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MethodDescriptionDictionary {

    private final Log log;
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Executable, MethodDescription> map = new ConcurrentHashMap<>();

    public MethodDescriptionDictionary(Log log) {
        this.log = log;
    }

    public MethodDescription get(Executable exec) {
        return map.computeIfAbsent(exec, e -> {
            long id = idGenerator.incrementAndGet();
            log.log(() -> "Method " + exec + " mapped to id " + id);
            return new MethodDescription(id, e);
        });
    }

    Collection<MethodDescription> getMethodInfos() {
        return map.values();
    }
}
