package com.ulyp.agent.util;

import com.ulyp.core.MethodDescriptionDictionary;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.Type;
import net.bytebuddy.description.type.TypeDescription;

public class ByteBuddyAgentRuntime implements AgentRuntime {

    private static class InstanceHolder {
        private static final ByteBuddyAgentRuntime context = new ByteBuddyAgentRuntime();
    }

    public static AgentRuntime getInstance() {
        return InstanceHolder.context;
    }

    @Override
    public long getClassId(Object o) {
        return o != null ? MethodDescriptionDictionary.getInstance().get(o.getClass()).getId() : -1;
    }

    @Override
    public Type toType(Class<?> clazz) {
        return new ByteBuddyType(TypeDescription.ForLoadedType.of(clazz).asGenericType());
    }
}
