package com.ulyp.core;

import com.ulyp.core.printers.TypeInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface AgentRuntime {

    @NotNull
    TypeInfo get(Object o);

    @NotNull
    TypeInfo get(Class<?> clazz);

    @NotNull
    Collection<TypeInfo> getAllKnownTypes();
}
