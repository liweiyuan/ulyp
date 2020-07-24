package com.ulyp.agent.util;

import com.ulyp.agent.settings.MethodRepresentation;
import net.bytebuddy.description.method.MethodDescription;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class MethodRepresentationBuilderTest {

    public interface Interface1 {

    }

    public interface Interface2 {

    }

    public interface Interface3 {

    }

    public static class BaseClass implements Interface1, Interface2 {

    }

    public static class TestClass extends BaseClass implements Interface3 {

        public void run() {

        }
    }

    @Test
    public void testForSomeClass() throws NoSuchMethodException {
        MethodRepresentation representation = MethodRepresentationBuilder.build(
                new MethodDescription.ForLoadedMethod(TestClass.class.getDeclaredMethod("run"))
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
    }
}