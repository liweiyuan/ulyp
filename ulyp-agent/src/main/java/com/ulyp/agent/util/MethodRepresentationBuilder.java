package com.ulyp.agent.util;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.Type;
import com.ulyp.core.util.ClassUtils;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MethodRepresentationBuilder {

    private static final AtomicLong counter = new AtomicLong();

    public static com.ulyp.core.MethodDescription newMethodDescription(MethodDescription description) {

        Set<String> superClassSimpleNames = new HashSet<>();
        Set<String> interfaceClassSimpleNames = new HashSet<>();

        TypeDefinition type = description.getDeclaringType();

        while (type != null && !type.getActualName().equals("java.lang.Object")) {
            superClassSimpleNames.add(ClassUtils.getSimpleNameFromName(type.getActualName()));

            for (TypeDescription.Generic interfface : type.getInterfaces()) {
                interfaceClassSimpleNames.add(ClassUtils.getSimpleNameFromName(interfface.getActualName()));
            }

            type = type.getSuperClass();
        }

        boolean returns = description.getReturnType() != TypeDescription.Generic.VOID;
        List<Type> parameters = description.getParameters().asTypeList().stream().map(ByteBuddyType::new).collect(Collectors.toList());
        Type returnType = new ByteBuddyType(description.getReturnType());


        ClassDescription classDescription = new ClassDescription(counter.incrementAndGet(),
                ClassUtils.getSimpleNameFromName(description.getDeclaringType().asGenericType().getActualName()),
                description.getDeclaringType().asGenericType().getActualName()
        );

        return new com.ulyp.core.MethodDescription(
                counter.incrementAndGet(),
                description.getActualName(),
                returns,
                superClassSimpleNames,
                interfaceClassSimpleNames,
                parameters,
                returnType,
                classDescription
        );
    }
}
