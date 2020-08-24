package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.StringObjectRepresentation;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TypeInfoTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(AtomicNumbersTestCases.class)
                        .setMethodToRecord("intSum")
        );

        assertThat(root.getArgs().get(0).getPrintedText(), is("2"));
        assertThat(root.getArgs().get(0).getType().getSimpleName(), is("AtomicInteger"));
        assertThat(root.getArgs().get(0).getType().getName(), is("java.util.concurrent.atomic.AtomicInteger"));

        assertThat(root.getArgs().get(1).getPrintedText(), is("3"));
        assertThat(root.getArgs().get(1).getType().getSimpleName(), is("AtomicLong"));
        assertThat(root.getArgs().get(1).getType().getName(), is("java.util.concurrent.atomic.AtomicLong"));

        assertThat(root.getReturnValue(), instanceOf(StringObjectRepresentation.class));
        assertThat(root.getReturnValue().getPrintedText(), is("5"));
    }

    public static class AtomicNumbersTestCases {

        public static String intSum(AtomicInteger v1, AtomicLong v2) {
            return String.valueOf(v1.get() + v2.get());
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> AtomicNumbersTestCases.intSum(new AtomicInteger(2), new AtomicLong(3)));
        }
    }
}
