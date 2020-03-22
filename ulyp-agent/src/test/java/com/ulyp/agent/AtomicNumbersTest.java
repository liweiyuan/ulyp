package com.ulyp.agent;

import com.test.cases.AtomicNumbersTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AtomicNumbersTest extends AbstractInstrumentationTest {

    @Test
    public void testAtomicIntegerSum() {
        MethodTraceTree tree = executeClass(
                AtomicNumbersTestCases.class,
                "com.test.cases",
                "AtomicNumbersTestCases.intSum"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue(), is("-211"));
    }

    @Test
    public void testBoxedDoubleSum() {
        MethodTraceTree tree = executeClass(
                AtomicNumbersTestCases.class,
                "com.test.cases",
                "AtomicNumbersTestCases.longSum"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue(), is("-211"));
    }
}
