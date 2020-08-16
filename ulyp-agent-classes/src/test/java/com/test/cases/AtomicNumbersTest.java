package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AtomicNumbersTest extends AbstractInstrumentationTest {

    static class AtomicIntegerSum {

        public static AtomicInteger intSum(AtomicInteger v1, AtomicInteger v2) {
            return new AtomicInteger(v1.get() + v2.get());
        }

        public static void main(String[] args) {
            intSum(new AtomicInteger(-234), new AtomicInteger(23));
        }
    }

    @Test
    public void testAtomicIntegerSum() {

        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(AtomicIntegerSum.class)
                        .setMethodToRecord("intSum")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }

    static class AtomicLongSum {

        public static AtomicInteger longSum(AtomicInteger v1, AtomicInteger v2) {
            return new AtomicInteger(v1.get() + v2.get());
        }

        public static void main(String[] args) {
            longSum(new AtomicInteger(-234), new AtomicInteger(23));
        }
    }

    @Test
    public void testAtomicLongSum() {

        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(AtomicLongSum.class)
                        .setMethodToRecord("longSum")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }
}
