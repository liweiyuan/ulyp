package com.ulyp.core.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassUtils {

    private static final ConcurrentMap<Class<?>, String> map = new ConcurrentHashMap<>(20000);

    public static String getSimpleName(Class<?> clazz) {
        return map.computeIfAbsent(clazz, ClassUtils::doGetSimpleName);
    }

    private static String doGetSimpleName(Class<?> clazz) {
        try {
            return clazz.getSimpleName();
        } catch (Exception e) {
            return "???";
        }
    }
}
