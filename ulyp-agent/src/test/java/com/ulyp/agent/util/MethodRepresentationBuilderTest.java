package com.ulyp.agent.util;

import net.bytebuddy.description.method.MethodDescription;
import org.junit.Assert;
import org.junit.Test;

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

        public Void runAndReturnVoid() {
            return null;
        }
    }

    @Test
    public void testForSomeClass() throws NoSuchMethodException {

        com.ulyp.core.MethodDescription methodDescription = MethodRepresentationBuilder.newMethodDescription(new MethodDescription.ForLoadedMethod(
                TestClass.class.getDeclaredMethod("run")
        ));

        Assert.assertFalse(methodDescription.returnsSomething());

        methodDescription = MethodRepresentationBuilder.newMethodDescription(new MethodDescription.ForLoadedMethod(
                TestClass.class.getDeclaredMethod("runAndReturnVoid")
        ));

        Assert.assertTrue(methodDescription.returnsSomething());
    }
}