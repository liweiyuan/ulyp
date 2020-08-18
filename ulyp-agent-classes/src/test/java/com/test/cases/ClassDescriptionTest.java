package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClassDescriptionTest extends AbstractInstrumentationTest {

    public static class AtomicNumbersTestCases {

        public static AtomicInteger intSum(AtomicInteger v1, AtomicInteger v2) {
            return new AtomicInteger(v1.get() + v2.get());
        }

        public static AtomicLong longSum(AtomicLong v1, AtomicLong v2) {
            return new AtomicLong(v1.get() + v2.get());
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> AtomicNumbersTestCases.intSum(new AtomicInteger(-234), new AtomicInteger(23)));
            SafeCaller.call(() -> AtomicNumbersTestCases.longSum(new AtomicLong(-234), new AtomicLong(23)));
        }
    }


    @Test
    public void shouldProvideArgumentTypes() {
        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(AtomicNumbersTestCases.class)
                        .setMethodToRecord("intSum")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("-234"));
        assertThat(root.getArgs().get(0).getType().getSimpleName(), is("AtomicInteger"));
        assertThat(root.getArgs().get(0).getType().getName(), is("java.util.concurrent.atomic.AtomicInteger"));

        assertThat(root.getArgs().get(1).getPrintedText(), is("23"));
        assertThat(root.getArgs().get(1).getType().getSimpleName(), is("AtomicInteger"));
        assertThat(root.getArgs().get(1).getType().getName(), is("java.util.concurrent.atomic.AtomicInteger"));
    }
}
