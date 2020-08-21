package com.ulyp.core;

import com.ulyp.core.printers.TypeInfo;

import java.util.Collection;

public interface AgentRuntime {

    TypeInfo get(Object o);

    Collection<TypeInfo> getAllKnownTypes();
}
