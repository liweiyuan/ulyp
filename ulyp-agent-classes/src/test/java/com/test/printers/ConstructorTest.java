package com.test.printers;

import com.test.cases.AbstractInstrumentationTest;
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
    public void test() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(TestCases.class)
                        .setMethodToRecord("main")
        );

        assertThat(root.getChildren(), Matchers.hasSize(1));

        CallRecord xConstructorCall = root.getChildren().get(0);

        assertThat(xConstructorCall.getMethodName(), Matchers.is("<init>"));
        assertThat(xConstructorCall.getClassName(), Matchers.is("com.test.printers.ConstructorTest$X"));

        assertThat(xConstructorCall.getChildren(), Matchers.hasSize(1));
    }
}
