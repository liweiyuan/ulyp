package com.ulyp.agent;

import com.test.cases.AtomicNumbersTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClassDescriptionTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        MethodTraceTree tree = executeClass(
                AtomicNumbersTestCases.class,
                "com.test.cases",
                "AtomicNumbersTestCases.intSum"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("-234", "23")));
        assertThat(root.getArgTypes(), is(Arrays.asList("java.util.concurrent.atomic.AtomicInteger", "java.util.concurrent.atomic.AtomicInteger")));
    }
}
