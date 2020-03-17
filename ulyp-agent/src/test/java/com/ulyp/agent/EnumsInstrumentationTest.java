package com.ulyp.agent;

import com.ulyp.agent.tests.EnumTestCases;
import com.ulyp.agent.transport.MethodTraceTree;
import com.ulyp.agent.transport.MethodTraceTreeBuilder;
import com.ulyp.agent.transport.MethodTraceTreeNode;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class EnumsInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintEnumNames() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                EnumTestCases.class,
                "com.ulyp.agent.tests",
                "EnumTestCases.consumesMapAndEnums"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(3));
    }
}
