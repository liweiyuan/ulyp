package com.ulyp.agent;

import com.ulyp.agent.util.AgentSettings;
import com.ulyp.agent.tests.SeveralMethodsTestCases;
import com.ulyp.agent.tests.SimpleTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class InstrumentationCodeTest extends AbstractInstrumentationTest {

    @Test
    public void shouldBeValidForStringReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = executeClass(
                SimpleTestCases.class,
                "com.ulyp.agent.tests",
                "SimpleTestCases.returnStringWithEmptyParams"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("'asdvdsa2'"));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnStringWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForNullReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = executeClass(
                SimpleTestCases.class,
                "com.ulyp.agent.tests",
                "SimpleTestCases.returnNullObjectWithEmptyParams"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("null"));
        assertThat(root.getResult(), is("null"));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnNullObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForIntReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = executeClass(
                SimpleTestCases.class,
                "com.ulyp.agent.tests",
                "SimpleTestCases.returnIntWithEmptyParams"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("124234232"));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnIntWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForTestObjectReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = executeClass(
                SimpleTestCases.class,
                "com.ulyp.agent.tests",
                "SimpleTestCases.returnTestObjectWithEmptyParams"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), matchesRegex("TestObject@\\d+"));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnTestObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidIfMethodThrowsException() {
        MethodTraceTree tree = executeClass(
                SimpleTestCases.class,
                "com.ulyp.agent.tests",
                "SimpleTestCases.throwsRuntimeException"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("RuntimeException: exception message"));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleTestCases"));
        assertThat(root.getMethodName(), is("throwsRuntimeException"));
    }

    @Test
    public void shouldBeValidForTwoMethodCalls() {
        MethodTraceTree tree = executeClass(
                SeveralMethodsTestCases.class,
                "com.ulyp.agent.tests",
                "SeveralMethodsTestCases.callTwoMethods"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(hasSize(2)));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("null"));
        assertThat(root.getResult(), is("void"));
        assertThat(root.getNodeCount(), is(3));
        assertThat(root.getMethodName(), is("callTwoMethods"));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SeveralMethodsTestCases"));

        MethodTraceTreeNode call1 = root.getChildren().get(0);

        assertThat(call1.getChildren(), is(empty()));
        assertThat(call1.getArgs(), is(empty()));
        assertThat(call1.getReturnValue(), is("null"));
        assertThat(call1.getResult(), is("void"));
        assertThat(call1.getNodeCount(), is(1));
        assertThat(call1.getMethodName(), is("method1"));

        MethodTraceTreeNode call2 = root.getChildren().get(1);

        assertThat(call2.getChildren(), is(empty()));
        assertThat(call2.getArgs(), is(empty()));
        assertThat(call2.getReturnValue(), is("null"));
        assertThat(call2.getResult(), is("void"));
        assertThat(call2.getNodeCount(), is(1));
        assertThat(call2.getMethodName(), is("method2"));
    }

    @Test
    public void shouldNotLogTracesIfMaxDepthExceeded() {
        MethodTraceTree tree = executeClass(
                new AgentSettings().setMainClassName(SeveralMethodsTestCases.class)
                .setPackages("com.ulyp.agent.tests")
                .setStartMethod("SeveralMethodsTestCases.callTwoMethods")
                .setMaxDepth(1)
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("null"));
        assertThat(root.getResult(), is("void"));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getMethodName(), is("callTwoMethods"));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SeveralMethodsTestCases"));
    }

    @Test
    public void shouldBeValidForIntArgument() {
        MethodTraceTree tree = executeClass(
                new AgentSettings().setMainClassName(SimpleTestCases.class)
                        .setPackages("com.ulyp.agent.tests")
                        .setStartMethod("SimpleTestCases.consumesInt")
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(Collections.singletonList("45324")));
        assertThat(root.getResult(), is("void"));
    }

    @Test
    public void shouldBeValidForIntAndStringArgument() {
        MethodTraceTree tree = executeClass(
                new AgentSettings().setMainClassName(SimpleTestCases.class)
                        .setPackages("com.ulyp.agent.tests")
                        .setStartMethod("SimpleTestCases.consumesMapAndEnums")
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getResult(), is("void"));
    }
}
