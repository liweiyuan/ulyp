package com.ulyp.agent;

import com.test.cases.RecursionTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RecursionInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void testFibonacciMethodCall() {
        MethodTraceTree tree = executeClass(
                RecursionTestCases.class,
                "com.test.cases",
                "RecursionTestCases.fibonacci"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getSubtreeNodeCount(), is(177));
    }
}
