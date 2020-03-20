package com.ulyp.agent;

import com.ulyp.agent.tests.UserDefinedClassTestCases;
import com.ulyp.core.MethodTraceTree;
import com.ulyp.core.MethodTraceTreeNode;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

public class UserDefinedClassInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintEnumNames() {
        MethodTraceTree tree = executeClass(
                UserDefinedClassTestCases.class,
                "com.ulyp.agent.tests",
                "UserDefinedClassTestCases.returnInnerClass"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getResult(), matchesPattern("TestClass@\\d+"));
    }
}
