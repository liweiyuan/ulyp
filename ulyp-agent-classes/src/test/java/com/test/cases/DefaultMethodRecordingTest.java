package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.util.MethodMatcher;
import org.junit.Assert;
import org.junit.Test;

public class DefaultMethodRecordingTest extends AbstractInstrumentationTest {

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
