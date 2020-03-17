package com.ulyp.agent;

import com.ulyp.agent.tests.BoxedNumbersCases;
import com.ulyp.agent.transport.MethodTraceTree;
import com.ulyp.agent.transport.MethodTraceTreeBuilder;
import com.ulyp.agent.transport.MethodTraceTreeNode;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BoxedNumbersTest extends InstrumentationTest {

    @Test
    public void testPrimitiveIntSum() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                BoxedNumbersCases.class,
                "com.ulyp.agent.tests",
                "BoxedNumbersCases.primitiveIntSum"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue(), is("-211"));
    }

    @Test
    public void testBoxedIntSum() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                BoxedNumbersCases.class,
                "com.ulyp.agent.tests",
                "BoxedNumbersCases.boxedIntSum"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue(), is("-211"));
    }

    @Test
    public void testPrimitiveDoubleSum() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                BoxedNumbersCases.class,
                "com.ulyp.agent.tests",
                "BoxedNumbersCases.primitiveDoubleSum"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue(), is("-5112.9847"));
    }

    @Test
    public void testBoxedDoubleSum() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                BoxedNumbersCases.class,
                "com.ulyp.agent.tests",
                "BoxedNumbersCases.boxedDoubleSum"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue(), is("-5112.9847"));
    }
}
