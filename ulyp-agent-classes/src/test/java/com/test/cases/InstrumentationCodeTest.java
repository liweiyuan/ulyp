package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class InstrumentationCodeTest extends AbstractInstrumentationTest {

    public static class MainMethodCase {

        public static void a() {

        }

        public static void main(String[] args) {
            a();
        }
    }

    @Test
    public void shouldRecordMainMethod() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(MainMethodCase.class)
                        .setMethodToRecord("main")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is("com.test.cases.InstrumentationCodeTest$MainMethodCase"));
        assertThat(root.getChildren(), hasSize(1));
    }


    public static class SimpleTestCases {

        public static class TestObject {}

        public SimpleTestCases.TestObject returnTestObjectWithEmptyParams() {
            return new SimpleTestCases.TestObject();
        }

        public String returnStringWithEmptyParams() {
            return "asdvdsa2";
        }

        public String returnNullObjectWithEmptyParams() {
            return null;
        }

        public int returnIntWithEmptyParams() {
            return 124234232;
        }

        public int throwsRuntimeException() {
            throw new RuntimeException("exception message");
        }

        public void consumesInt(int v) {
        }

        public void consumesIntAndString(int v, String s) {
        }

        public static void staticMethod() {}

        public static void main(String[] args) {
            SafeCaller.call(() -> new SimpleTestCases().returnIntWithEmptyParams());
            SafeCaller.call(() -> new SimpleTestCases().returnTestObjectWithEmptyParams());
            SafeCaller.call(() -> new SimpleTestCases().returnStringWithEmptyParams());
            SafeCaller.call(() -> new SimpleTestCases().returnNullObjectWithEmptyParams());
            SafeCaller.call(() -> new SimpleTestCases().throwsRuntimeException());
            SafeCaller.call(() -> new SimpleTestCases().consumesInt(45324));
            SafeCaller.call(() -> new SimpleTestCases().consumesIntAndString(45324, "asdasd"));
            staticMethod();
        }
    }


    @Test
    public void shouldTraceStaticMethodCall() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToRecord("staticMethod")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getMethodName(), is("staticMethod"));
        assertThat(root.getArgTexts(), empty());
    }

    @Test
    public void shouldBeValidForStringReturningMethodWithEmptyArgs() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToRecord("returnStringWithEmptyParams")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("asdvdsa2"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.InstrumentationCodeTest$SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnStringWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForNullReturningMethodWithEmptyArgs() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToRecord("returnNullObjectWithEmptyParams")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        // TODO use NullRepr
        assertThat(root.getReturnValue().getPrintedText(), is("null"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.InstrumentationCodeTest$SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnNullObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForIntReturningMethodWithEmptyArgs() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToRecord("returnIntWithEmptyParams")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("124234232"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.InstrumentationCodeTest$SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnIntWithEmptyParams"));
    }

    @Test
    public void shouldBeValidForTestObjectReturningMethodWithEmptyArgs() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToRecord("returnTestObjectWithEmptyParams")
        );


        CallRecord root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), matchesRegex("TestObject@\\d+"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.InstrumentationCodeTest$SimpleTestCases"));
        assertThat(root.getMethodName(), is("returnTestObjectWithEmptyParams"));
    }

    @Test
    public void shouldBeValidIfMethodThrowsException() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToRecord("throwsRuntimeException")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("RuntimeException: exception message"));
        assertThat(root.getSubtreeNodeCount(), is(1));
        assertThat(root.getClassName(), is("com.test.cases.InstrumentationCodeTest$SimpleTestCases"));
        assertThat(root.getMethodName(), is("throwsRuntimeException"));
    }

    public static class SeveralMethodsTestCases {

        public void callTwoMethods() {
            method1();
            method2();
        }

        public void method1() {
            System.out.println("b");
        }

        public void method2() {
            System.out.println("c");
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> new SeveralMethodsTestCases().callTwoMethods());
        }
    }

    @Test
    public void shouldBeValidForTwoMethodCalls() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SeveralMethodsTestCases.class)
                        .setMethodToRecord("callTwoMethods")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getChildren(), is(hasSize(2)));
        assertThat(root.getArgTexts(), is(empty()));
        assertThat(root.getReturnValue().getPrintedText(), is("null"));
        assertThat(root.isVoidMethod(), is(Boolean.TRUE));
        assertThat(root.getSubtreeNodeCount(), is(3));
        assertThat(root.getMethodName(), is("callTwoMethods"));
        assertThat(root.getClassName(), is("com.test.cases.InstrumentationCodeTest$SeveralMethodsTestCases"));

        CallRecord call1 = root.getChildren().get(0);

        assertThat(call1.getChildren(), is(empty()));
        assertThat(call1.getArgs(), is(empty()));
        assertThat(call1.getReturnValue().getPrintedText(), is("null"));
        assertThat(call1.isVoidMethod(), is(Boolean.TRUE));
        assertThat(call1.getSubtreeNodeCount(), is(1));
        assertThat(call1.getMethodName(), is("method1"));

        CallRecord call2 = root.getChildren().get(1);

        assertThat(call2.getChildren(), is(empty()));
        assertThat(call2.getArgs(), is(empty()));
        assertThat(call2.getReturnValue().getPrintedText(), is("null"));
        assertThat(call2.isVoidMethod(), is(Boolean.TRUE));
        assertThat(call2.getSubtreeNodeCount(), is(1));
        assertThat(call2.getMethodName(), is("method2"));
    }

    @Test
    public void shouldBeValidForIntArgument() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(SimpleTestCases.class)
                        .setMethodToRecord("consumesInt")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getChildren(), is(empty()));
        assertThat(root.getArgTexts(), is(Collections.singletonList("45324")));
    }
}
