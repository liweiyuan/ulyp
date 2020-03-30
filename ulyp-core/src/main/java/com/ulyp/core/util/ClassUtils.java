package com.ulyp.core.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassUtils {

    private static final ConcurrentMap<Class<?>, String> clazzToSimpleName = new ConcurrentHashMap<>(20000);

    public static String getSimpleName(Class<?> clazz) {
        return clazzToSimpleName.computeIfAbsent(clazz, ClassUtils::getSimpleNameSafe);
    }

    public static String getSimpleNameSafe(Class<?> clazz) {
        try {
            return clazz.getSimpleName();
        } catch (Exception e) {
            return "???";
        }
    }
}
