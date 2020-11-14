package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RecursionInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void testFibonacciMethodCall() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(RecursionTestCases.class)
                        .setMethodToRecord("fibonacci")
        );

        assertThat(root.getSubtreeNodeCount(), is(177L));
    }

    public static class RecursionTestCases {

        public static void main(String[] args) {
            SafeCaller.call(() -> new RecursionTestCases().fibonacci(10));
        }

        public int fibonacci(int v) {
            if (v <= 1) {
                return v;
            }
            return fibonacci(v - 1) + fibonacci(v - 2);
        }
    }
}
