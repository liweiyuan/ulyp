package com.ulyp.agent;

import com.ulyp.agent.tests.SeveralMethodsTestCases;
import com.ulyp.core.MethodTraceTree;
import com.ulyp.core.MethodTraceTreeNode;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RecursionInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void testFibonacciMethodCall() {
        MethodTraceTree tree = executeClass(
                SeveralMethodsTestCases.class,
                "com.ulyp.agent.tests",
                "SeveralMethodsTestCases.fibonacci"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getNodeCount(), is(177));
    }
}
