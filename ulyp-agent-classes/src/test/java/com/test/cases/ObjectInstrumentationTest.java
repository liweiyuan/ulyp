package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

public class ObjectInstrumentationTest extends AbstractInstrumentationTest {

    public static class ObjectTestCases {

        private static volatile Object store0;
        private static volatile Object store1;
        private static volatile Object store2;
        private static volatile Object store3;

        public void acceptsTwoObjects(Object o1, Object o2) {
            store0 = o1;
            store1 = o2;
        }

        public void acceptsTwoObjects2(Object o1, Object o2) {
            store0 = o1;
            store1 = o2;
        }

        public void acceptsTwoObjects3(Object o1, Object o2) {
            store0 = o1;
            store1 = o2;
        }

        public void acceptsTwoNulls(Object o1, Object o2) {
            store0 = o1;
            store1 = o2;
        }

        private static class X {}

        private static class Y {
            public String toString() {
                return "Y{}";
            }
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects(new Object(), new Object()));
            SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects2("asdasd", 34));
            SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects3(new ObjectTestCases.X(), new ObjectTestCases.Y()));
            SafeCaller.call(() -> new ObjectTestCases().acceptsTwoNulls(null, null));
        }
    }

    @Test
    public void shouldPrintObjects() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToRecord("acceptsTwoObjects")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0).getPrintedText(), matchesPattern("Object@.+"));
    }

    @Test
    public void shouldChooseValidPrinterForJavaLangObjectAtRuntime() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToRecord("acceptsTwoObjects2")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0).getPrintedText(), is("asdasd"));
        assertThat(root.getArgs().get(1).getPrintedText(), is("34"));
    }

    @Test
    public void shouldCallToStringIfPossible() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToRecord("acceptsTwoObjects3")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0).getPrintedText(), matchesPattern("X@.+"));
        assertThat(root.getArgs().get(1).getPrintedText(), is("Y{}"));
    }

    @Test
    public void shouldPrintNullArguments() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToRecord("acceptsTwoNulls")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgTexts().get(0), is("null"));
        assertThat(root.getArgTexts().get(1), is("null"));
    }
}
