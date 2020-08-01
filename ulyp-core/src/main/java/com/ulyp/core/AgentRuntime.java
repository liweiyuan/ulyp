package com.ulyp.core;

import com.ulyp.core.printers.Type;

public interface AgentRuntime {

    long getClassId(Object o);

    Type toType(Class<?> clazz);
}
