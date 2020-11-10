package com.test.cases;

import com.test.cases.a.A;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.util.MethodMatcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RecordingTest extends AbstractInstrumentationTest {

    @Test
    public void shouldRecordMainMethodIfMatcherIsNotSpecified() {
        CallRecord root = runSubprocessWithUi(new TestSettingsBuilder().setMainClassName(RecursionTestCases.class));

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is(RecursionTestCases.class.getName()));
        assertThat(root.getChildren(), Matchers.hasSize(1));
    }

    @Test
    public void testRecordViaInterfaceMatcher() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(RecursionTestCases.class)
                        .setMethodToRecord(MethodMatcher.parse("Interface.foo"))
        );

        Assert.assertNotNull(root);
    }

    public interface Interface {

        default int foo() {
            return 42;
        }
    }

    public static class Clazz implements Interface {

    }

    public static class RecursionTestCases {

        public static void main(String[] args) {
            System.out.println(new Clazz().foo());
        }
    }
}
