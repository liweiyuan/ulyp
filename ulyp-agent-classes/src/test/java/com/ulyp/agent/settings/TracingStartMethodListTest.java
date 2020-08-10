/*
package com.ulyp.agent.settings;

import com.ulyp.core.util.MethodMatcher;
import net.bytebuddy.description.method.MethodDescription;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class TracingStartMethodListTest {

    public static class A { public void run() {}}

    private TracingStartMethodList tracingStartMethodList;

    private void assertStartTracingAt(Method method) {
        assertTrue(tracingStartMethodList.shouldStartTracing(
                MethodRepresentationBuilder.buildAs(new MethodDescription.ForLoadedMethod(method))
        ));
    }

    private void assertDoesNotStartTracingAt(Method method) {
        assertFalse(tracingStartMethodList.shouldStartTracing(
                MethodRepresentationBuilder.buildAs(new MethodDescription.ForLoadedMethod(method))
        ));
    }

    @Test
    public void testNestedClass() throws NoSuchMethodException {
        tracingStartMethodList = new TracingStartMethodList(
                new MethodMatcher("A", "run")
        );

        assertStartTracingAt(A.class.getMethod("run"));
    }

    @Test
    public void testMatchersOnInfrastructure() throws NoSuchMethodException {
        tracingStartMethodList = new TracingStartMethodList(
                new MethodMatcher("TestClass", "a")
        );

        assertStartTracingAt(TestClass.class.getMethod("a"));

        assertStartTracingAt(TestClass.class.getMethod("a", String.class));

        assertDoesNotStartTracingAt(TestClass.class.getMethod("b"));
    }

    @Test
    public void testMatchingOnBaseClass() throws NoSuchMethodException {
        tracingStartMethodList = new TracingStartMethodList(
                new MethodMatcher("BaseClass", "a")
        );

        assertStartTracingAt(BaseClass.class.getMethod("a"));

        assertStartTracingAt(BaseClass.class.getMethod("a", String.class));

        assertDoesNotStartTracingAt(BaseClass.class.getMethod("b"));

        assertStartTracingAt(TestClass.class.getMethod("a"));

        assertStartTracingAt(TestClass.class.getMethod("a", String.class));
    }

    @Test
    public void testMatchingOnBaseBaseClass() throws NoSuchMethodException {
        tracingStartMethodList = new TracingStartMethodList(
                new MethodMatcher("BaseBaseClass", "x")
        );

        assertDoesNotStartTracingAt(BaseClass.class.getMethod("a"));

        assertDoesNotStartTracingAt(BaseClass.class.getMethod("a", String.class));

        assertDoesNotStartTracingAt(BaseClass.class.getMethod("b"));

        assertStartTracingAt(BaseBaseClass.class.getMethod("x"));

        assertStartTracingAt(TestClass.class.getMethod("x"));
    }

    @Test
    public void testMatchingOnInterface() throws NoSuchMethodException {
        tracingStartMethodList = new TracingStartMethodList(
                new MethodMatcher("Interface1", "a")
        );

        assertStartTracingAt(TestClass.class.getMethod("a", String.class));
    }
}
*/
