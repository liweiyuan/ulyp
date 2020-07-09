package com.ulyp.agent;

import com.test.cases.UserDefinedClassTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

public class UserDefinedClassInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintEnumNames() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(UserDefinedClassTestCases.class)
                        .setMethodToTrace("returnInnerClass")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getResult(), matchesPattern("TestClass@\\d+"));
    }

    @Test
    public void shouldNotFailIfToStringCallsTracedMethod() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(UserDefinedClassTestCases.class)
                        .setMethodToTrace("returnClassThatCallsSelfInToString")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getResult(), is("ToStringCallsSelf{name='ToStringCallsSelf{name='n1', secondName='s1'}ToStringCallsSelf{name='n1', secondName='s1'}', secondName='ToStringCallsSelf{name='n1', secondName='s1'}/ToStringCallsSelf{name='n1', secondName='s1'}'}"));
    }
}
