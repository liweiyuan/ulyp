package com.ulyp.agent;

import com.test.cases.BoxedNumbersTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BoxedNumbersTest extends AbstractInstrumentationTest {

    @Test
    public void testPrimitiveIntSum() {
        MethodTraceTree tree = executeClass(
                BoxedNumbersTestCases.class,
                "com.test.cases",
                "BoxedNumbersTestCases.primitiveIntSum"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue(), is("-211"));
    }

    @Test
    public void testBoxedIntSum() {
        MethodTraceTree tree = executeClass(
                BoxedNumbersTestCases.class,
                "com.test.cases",
                "BoxedNumbersTestCases.boxedIntSum"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue(), is("-211"));
    }

    @Test
    public void testPrimitiveDoubleSum() {
        MethodTraceTree tree = executeClass(
                BoxedNumbersTestCases.class,
                "com.test.cases",
                "BoxedNumbersTestCases.primitiveDoubleSum"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue(), is("-5112.9847"));
    }

    @Test
    public void testBoxedDoubleSum() {
        MethodTraceTree tree = executeClass(
                BoxedNumbersTestCases.class,
                "com.test.cases",
                "BoxedNumbersTestCases.boxedDoubleSum"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue(), is("-5112.9847"));
    }
}
