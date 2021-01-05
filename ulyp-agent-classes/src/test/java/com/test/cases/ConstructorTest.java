package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class ConstructorTest extends AbstractInstrumentationTest {

    public static class Base {

    }

    public static class X extends Base {

        public X() {
            System.out.println(43);
        }
    }

    public static class TestCases {

        public static void main(String[] args) {
            System.out.println(new X());
        }
    }

    @Test
    public void testHappyPathConstructor() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(TestCases.class)
                        .setMethodToRecord("main")
        );

        assertThat(root.getChildren(), Matchers.hasSize(1));

        CallRecord xConstructorCall = root.getChildren().get(0);

        assertThat(xConstructorCall.getMethodName(), Matchers.is("<init>"));
        assertThat(xConstructorCall.getClassName(), Matchers.is("com.test.cases.ConstructorTest$X"));

        assertThat(xConstructorCall.getChildren(), Matchers.hasSize(1));
    }

    public static class X3 extends Base {

        public X3() {
            throw new RuntimeException("a");
        }
    }

    public static class TestCasesThrows {

        public static void main(String[] args) {
            try {
                System.out.println(new X3());
            } catch (Exception e) {

            }
        }
    }

    @Test
    public void testConstructorThrown() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(TestCasesThrows.class)
                        .setMethodToRecord("main")
        );

        assertThat(root.getChildren(), Matchers.hasSize(1));

        CallRecord ctr = root.getChildren().get(0);

        assertThat(ctr.getMethodName(), Matchers.is("<init>"));
        assertThat(ctr.getClassName(), Matchers.is("com.test.cases.ConstructorTest$X3"));
    }
}
