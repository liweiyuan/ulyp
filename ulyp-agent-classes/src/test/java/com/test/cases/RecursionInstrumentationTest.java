package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RecursionInstrumentationTest extends AbstractInstrumentationTest {

    public static class RecursionTestCases {

        public int fibonacci(int v) {
            if (v <= 1) {
                return v;
            }
            return fibonacci(v - 1) + fibonacci(v - 2);
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> new RecursionTestCases().fibonacci(10));
        }
    }

    @Test
    public void testFibonacciMethodCall() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(RecursionTestCases.class)
                        .setMethodToTrace("fibonacci")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getSubtreeNodeCount(), is(177));
    }
}
