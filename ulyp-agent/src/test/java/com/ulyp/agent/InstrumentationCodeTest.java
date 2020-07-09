package com.ulyp.agent;

import com.ulyp.agent.util.TestSettingsBuilder;
import com.test.cases.SeveralMethodsTestCases;
import com.test.cases.SimpleTestCases;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class InstrumentationCodeTest extends AbstractInstrumentationTest {

    @Test
    public void shouldTraceStaticMethodCall() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToTrace("staticMethod")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getMethodName(), is("staticMethod"));
        assertThat(root.getArgTexts(), empty());
    }

    @Test
    public void shouldBeValidForStringReturningMethodWithEmptyArgs() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToTrace("returnStringWithEmptyParams")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("asdvdsa2"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnStringWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForNullReturningMethodWithEmptyArgs() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToTrace("returnNullObjectWithEmptyParams")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("null"));
        assertThat(root.getResult(), is("null"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnNullObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForIntReturningMethodWithEmptyArgs() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToTrace("returnIntWithEmptyParams")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("124234232"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnIntWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForTestObjectReturningMethodWithEmptyArgs() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToTrace("returnTestObjectWithEmptyParams")
        );


        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), matchesRegex("TestObject@\\d+"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnTestObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidIfMethodThrowsException() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToTrace("throwsRuntimeException")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("RuntimeException: exception message"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.SimpleTestCases"));
        assertThat(root.getMethodName(), is("throwsRuntimeException"));
    }

    @Test
    public void shouldBeValidForTwoMethodCalls() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SeveralMethodsTestCases.class)
                        .setMethodToTrace("callTwoMethods")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), is(hasSize(2)));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("null"));
        assertThat(root.getResult(), is("void"));
        assertThat(root.getSubtreeNodeCount(), is(3));
        assertThat(root.getMethodName(), is("callTwoMethods"));
        assertThat(root.getClassName(), is("com.test.cases.SeveralMethodsTestCases"));

        CallTrace call1 = root.getChildren().get(0);

        assertThat(call1.getChildren(), is(empty()));
        assertThat(call1.getArgs(), is(empty()));
        assertThat(call1.getReturnValue().getPrintedText(), is("null"));
        assertThat(call1.getResult(), is("void"));
        assertThat(call1.getSubtreeNodeCount(), is(1));
        assertThat(call1.getMethodName(), is("method1"));

        CallTrace call2 = root.getChildren().get(1);

        assertThat(call2.getChildren(), is(empty()));
        assertThat(call2.getArgs(), is(empty()));
        assertThat(call2.getReturnValue().getPrintedText(), is("null"));
        assertThat(call2.getResult(), is("void"));
        assertThat(call2.getSubtreeNodeCount(), is(1));
        assertThat(call2.getMethodName(), is("method2"));
    }

    @Test
    public void shouldBeValidForIntArgument() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToTrace("consumesInt")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(Collections.singletonList("45324")));
        assertThat(root.getResult(), is("void"));
    }
}
