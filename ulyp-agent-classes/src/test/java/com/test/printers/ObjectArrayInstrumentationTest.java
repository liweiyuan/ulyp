package com.test.printers;

import com.test.cases.AbstractInstrumentationTest;
import com.test.cases.SafeCaller;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ObjectArrayInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToRecord("acceptEmptyObjectArray")
        );

        assertThat(root.getArgs().get(0).getPrintedText(), is("Object[]"));
    }

    @Test
    public void testUserDefinedEmptyArray() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToRecord("acceptEmptyUserDefinedClassArray")
        );

        assertThat(root.getArgs().get(0).getPrintedText(), is("X[]"));
    }

    @Test
    public void testUserDefinedClassArrayWith3Elements() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToRecord("acceptUserDefinedClassArrayWith3Elements")
        );

        assertThat(root.getArgs().get(0).getPrintedText(), is("X[][3 items ]"));
    }

    @Test
    public void testUserDefinedClassArrayWith3ElementsWithArrayTrace() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setRecordCollectionItems(true)
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToRecord("acceptUserDefinedClassArrayWith3Elements")
        );

        assertThat(root.getArgs().get(0).getPrintedText(), is("[X{text='a'}, null, X{text='b'}]"));
    }

    public static class ObjectArrayTestCases {

        public static void main(String[] args) {
            SafeCaller.call(() -> new ObjectArrayTestCases().acceptEmptyObjectArray(new Object[]{}));
            SafeCaller.call(() -> new ObjectArrayTestCases().acceptEmptyUserDefinedClassArray(new X[]{}));
            SafeCaller.call(() -> new ObjectArrayTestCases().acceptUserDefinedClassArrayWith3Elements(
                    new X[]{new X("a"), null, new X("b")}
                    )
            );
        }

        public void acceptEmptyObjectArray(Object[] array) {
        }

        public void acceptEmptyUserDefinedClassArray(X[] array) {
        }

        public void acceptUserDefinedClassArrayWith3Elements(X[] array) {
        }

        private static class X {
            private final String text;

            private X(String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return "X{" +
                        "text='" + text + '\'' +
                        '}';
            }
        }
    }
}
