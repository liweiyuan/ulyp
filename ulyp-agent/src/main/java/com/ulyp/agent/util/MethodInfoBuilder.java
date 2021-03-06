package com.ulyp.agent.util;

import com.ulyp.core.MethodInfo;
import com.ulyp.core.printers.TypeInfo;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MethodInfoBuilder {

    private static final AtomicInteger counter = new AtomicInteger();

    public static MethodInfo newMethodDescription(MethodDescription description) {
        try {
            boolean returns = !description.getReturnType().asGenericType().equals(TypeDescription.Generic.VOID);
            List<TypeInfo> parameters = description.getParameters().asTypeList().stream().map(ByteBuddyTypeInfo::of).collect(Collectors.toList());
            TypeInfo returnTypeInfo = ByteBuddyTypeInfo.of(description.getReturnType());
            TypeInfo declaringType = ByteBuddyTypeInfo.of(description.getDeclaringType().asGenericType());

            return new MethodInfo(
                    counter.incrementAndGet(),
                    description.getActualName(),
                    description.isConstructor(),
                    description.isStatic(),
                    returns,
                    parameters,
                    returnTypeInfo,
                    declaringType
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
