package com.ulyp.agent.util;

import com.ulyp.core.printers.Type;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MethodDescriptionBuilder {

    private static final AtomicLong counter = new AtomicLong();

    public static com.ulyp.core.MethodDescription newMethodDescription(MethodDescription description) {

        boolean returns = !description.getReturnType().asGenericType().equals(TypeDescription.Generic.VOID);
        List<Type> parameters = description.getParameters().asTypeList().stream().map(ByteBuddyType::new).collect(Collectors.toList());
        Type returnType = new ByteBuddyType(description.getReturnType());

        ByteBuddyType declaringType = new ByteBuddyType(description.getDeclaringType().asGenericType());

        com.ulyp.core.MethodDescription methodDescription = new com.ulyp.core.MethodDescription(
                counter.incrementAndGet(),
                description.getActualName(),
                description.isStatic(),
                returns,
                parameters,
                returnType,
                declaringType
        );

//        if (methodDescription.getMethodName().equals("createStatement")) {
//            if (declaringType.getName().contains("Connection")) {
//                System.out.println("* " + declaringType.getInterfacesSimpleClassNames());
//                System.out.println("** " + declaringType.getSuperClassesSimpleNames());
//                System.out.println("*** start tracing: " + recordingStartMethodList.shouldStartTracing(methodDescription));
//            }
//        }

        return methodDescription;
    }
}
