package com.ulyp.agent;

import com.test.cases.RecursionTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RecursionInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void testFibonacciMethodCall() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(RecursionTestCases.class)
                        .setMethodToTrace("fibonacci")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getSubtreeNodeCount(), is(177));
    }
}
