package com.ulyp.agent.util;

import net.bytebuddy.description.method.MethodDescription;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class ByteBuddyTypeTest {

    @Test
    public void test() {
        /*
        MethodRepresentation representation = MethodRepresentationBuilder.build(
            new MethodDescription.ForLoadedMethod(MethodRepresentationBuilderTest.TestClass.class.getDeclaredMethod("run"))
    );

    assertEquals(
                new HashSet<String>() {{
        add("TestClass");
        add("BaseClass");
    }},
            representation.getSuperClassesSimpleNames()
            );

    assertEquals(
                new HashSet<String>() {{
        add("Interface1");
        add("Interface2");
        add("Interface3");
    }},
            representation.getInterfacesSimpleClassNames()
            );
        */
    }
}