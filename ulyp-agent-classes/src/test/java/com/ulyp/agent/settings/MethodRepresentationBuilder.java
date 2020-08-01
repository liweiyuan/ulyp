/*
package com.ulyp.agent.settings;

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

    public static com.ulyp.core.MethodDescription buildAs(MethodDescription description) {

        Set<String> superClassesNames = new HashSet<>();
        Set<String> interfacesClassNames = new HashSet<>();

        TypeDefinition type = description.getDeclaringType();

        while (type != null && !type.getActualName().equals("java.lang.Object")) {
            superClassesNames.add(type.getActualName());

            for (TypeDescription.Generic interfface : type.getInterfaces()) {
                interfacesClassNames.add(interfface.getActualName());
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
                superClassesNames,
                interfacesClassNames,
                parameters,
                returnType
        );
    }
}
*/
