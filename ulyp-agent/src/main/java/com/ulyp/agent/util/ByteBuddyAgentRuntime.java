package com.ulyp.agent.util;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.core.printers.UnknownTypeInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ByteBuddyAgentRuntime implements AgentRuntime {

    private static class InstanceHolder {
        private static final ByteBuddyAgentRuntime context = new ByteBuddyAgentRuntime();
    }

    public static AgentRuntime getInstance() {
        return InstanceHolder.context;
    }

    private final Map<Class<?>, TypeInfo> classDescriptionMap = new ConcurrentHashMap<>();

    @Override
    public TypeInfo get(Object o) {
        if (o != null) {
            return classDescriptionMap.computeIfAbsent(
                    o.getClass(),
                    ByteBuddyTypeInfo::new
            );
        } else {
            return UnknownTypeInfo.getInstance();
        }
    }

    @Override
    public Collection<TypeInfo> getAllKnownTypes() {
        return classDescriptionMap.values();
    }
}
