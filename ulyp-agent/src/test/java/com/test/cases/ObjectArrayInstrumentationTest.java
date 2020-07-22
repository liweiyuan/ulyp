package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ObjectArrayInstrumentationTest extends AbstractInstrumentationTest {

    public static class ObjectArrayTestCases {

        public void acceptEmptyObjectArray(Object[] array) {
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

        public void acceptEmptyUserDefinedClassArray(X[] array) {
        }

        public void acceptUserDefinedClassArrayWith3Elements(X[] array) {
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> new ObjectArrayTestCases().acceptEmptyObjectArray(new Object[]{}));
            SafeCaller.call(() -> new ObjectArrayTestCases().acceptEmptyUserDefinedClassArray(new X[]{}));
            SafeCaller.call(() -> new ObjectArrayTestCases().acceptUserDefinedClassArrayWith3Elements(
                    new X[]{new X("a"), null, new X("b")}
                    )
            );
        }
    }


    @Test
    public void shouldProvideArgumentTypes() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptEmptyObjectArray")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("Object[]"));
    }

    @Test
    public void testUserDefinedEmptyArray() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptEmptyUserDefinedClassArray")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("X[]"));
    }

    @Test
    public void testUserDefinedClassArrayWith3Elements() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptUserDefinedClassArrayWith3Elements")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("X[][3 items ]"));
    }

    @Test
    public void testUserDefinedClassArrayWith3ElementsWithArrayTrace() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setTraceCollections(true)
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptUserDefinedClassArrayWith3Elements")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("[X{text='a'}, null, X{text='b'}]"));
    }
}
