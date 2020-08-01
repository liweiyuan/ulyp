package com.ulyp.agent.util;

import com.ulyp.core.MethodDescriptionDictionary;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.Type;
import net.bytebuddy.description.type.TypeDescription;

public class ByteBuddyTracingContext implements TracingContext {

    private static class InstanceHolder {
        private static final ByteBuddyTracingContext context = new ByteBuddyTracingContext();
    }

    public static TracingContext getInstance() {
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
