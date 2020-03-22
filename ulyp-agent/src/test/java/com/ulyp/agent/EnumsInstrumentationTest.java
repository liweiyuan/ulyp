package com.ulyp.agent;

import com.test.cases.EnumTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnumsInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintEnumNames() {
        MethodTraceTree tree = executeClass(
                EnumTestCases.class,
                "com.test.cases",
                "EnumTestCases.consumesMapAndEnums"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(3));
        assertThat(root.getArgs().get(1), is("T1"));
        assertThat(root.getArgs().get(2), is("T2"));
    }
}
