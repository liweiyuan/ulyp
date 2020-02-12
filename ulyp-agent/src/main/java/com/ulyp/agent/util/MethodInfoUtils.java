package com.ulyp.agent.util;

import com.ulyp.transport.TMethodInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public class MethodInfoUtils {

    private MethodInfoUtils() {}

    public static TMethodInfo of(long id, Executable executable) {
        return TMethodInfo.newBuilder()
                .setId(id)
                .setClassName(executable.getDeclaringClass().getName())
                .setMethodName(executable instanceof Constructor ? "<init>" : executable.getName())
                .setReturnsSomething(!(executable instanceof Method) || !((Method) executable).getReturnType().equals(Void.TYPE))
                .build();
    }
}
