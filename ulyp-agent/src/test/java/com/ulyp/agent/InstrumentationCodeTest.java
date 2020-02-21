package com.ulyp.agent;

import com.ulyp.agent.tests.SeveralMethodsCases;
import com.ulyp.agent.tests.SimpleCases;
import com.ulyp.agent.transport.MethodTraceTree;
import com.ulyp.agent.transport.MethodTraceTreeBuilder;
import com.ulyp.agent.transport.MethodTraceTreeNode;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class InstrumentationCodeTest extends InstrumentationTest {

    @Test
    public void shouldReturnValidMainClassName() {
        TMethodTraceLogUploadRequest request = executeClass(SimpleCases.class, "com.ulyp.agent.tests", "SimpleCases.returnIntWithEmptyParams");

        Assert.assertEquals(SimpleCases.class.getName(), request.getMainClassName());
    }

    @Test
    public void shouldBeValidForStringReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SimpleCases.class,
                "com.ulyp.agent.tests",
                "SimpleCases.returnStringWithEmptyParams"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("'asdvdsa2'"));
        assertThat(root.getThrownValue(), is(emptyString()));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleCases"));
        assertThat(root.getMethodName(), is("returnStringWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForNullReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SimpleCases.class,
                "com.ulyp.agent.tests",
                "SimpleCases.returnNullObjectWithEmptyParams"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is(emptyString()));
        assertThat(root.getThrownValue(), is(emptyString()));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleCases"));
        assertThat(root.getMethodName(), is("returnNullObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForIntReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SimpleCases.class,
                "com.ulyp.agent.tests",
                "SimpleCases.returnIntWithEmptyParams"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is("124234232"));
        assertThat(root.getThrownValue(), is(emptyString()));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleCases"));
        assertThat(root.getMethodName(), is("returnIntWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForTestObjectReturningMethodWithEmptyArgs() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SimpleCases.class,
                "com.ulyp.agent.tests",
                "SimpleCases.returnTestObjectWithEmptyParams"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), matchesRegex("TestObject@\\d+"));
        assertThat(root.getThrownValue(), is(emptyString()));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleCases"));
        assertThat(root.getMethodName(), is("returnTestObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidIfMethodThrowsException() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SimpleCases.class,
                "com.ulyp.agent.tests",
                "SimpleCases.throwsRuntimeException"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is(emptyString()));
        assertThat(root.getThrownValue(), is("RuntimeException: 'exception message'"));
        assertThat(root.getNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SimpleCases"));
        assertThat(root.getMethodName(), is("throwsRuntimeException"));
    }

    @Test
    public void shouldBeValidForTwoMethodCalls() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SeveralMethodsCases.class,
                "com.ulyp.agent.tests",
                "SeveralMethodsCases.callTwoMethods"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getChildren(), is(hasSize(2)));
        assertThat(root.getArgs(), is(empty()));
        assertThat(root.getReturnValue(), is(emptyString()));
        assertThat(root.getThrownValue(), is(emptyString()));
        assertThat(root.getResult(), is("void"));
        assertThat(root.getNodeCount(), is(3));
        assertThat(root.getMethodName(), is("callTwoMethods"));
        assertThat(root.getClassName(), is("com.ulyp.agent.tests.SeveralMethodsCases"));

        MethodTraceTreeNode call1 = root.getChildren().get(0);

        assertThat(call1.getChildren(), is(empty()));
        assertThat(call1.getArgs(), is(empty()));
        assertThat(call1.getReturnValue(), is(emptyString()));
        assertThat(call1.getThrownValue(), is(emptyString()));
        assertThat(call1.getResult(), is("void"));
        assertThat(call1.getNodeCount(), is(1));
        assertThat(call1.getMethodName(), is("method1"));

        MethodTraceTreeNode call2 = root.getChildren().get(1);

        assertThat(call2.getChildren(), is(empty()));
        assertThat(call2.getArgs(), is(empty()));
        assertThat(call2.getReturnValue(), is(emptyString()));
        assertThat(call2.getThrownValue(), is(emptyString()));
        assertThat(call2.getResult(), is("void"));
        assertThat(call2.getNodeCount(), is(1));
        assertThat(call2.getMethodName(), is("method2"));
    }

    @Test
    public void shouldBeValidForManyMethodCalls() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SeveralMethodsCases.class,
                "com.ulyp.agent.tests",
                "SeveralMethodsCases.fibonacci"
        ));

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getNodeCount(), is(177));
    }
}
