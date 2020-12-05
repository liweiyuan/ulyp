package com.test.printers;

import com.test.cases.AbstractInstrumentationTest;
import com.test.cases.SafeCaller;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.PlainObjectRepresentation;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ExceptionTest extends AbstractInstrumentationTest {

    public static class TestCases {

        public static int alwaysThrow(int v1, int v2) {
            throw new RuntimeException("asdasdaghdajd");
        }

        public static int alwaysThrowWithLargeMessage(int v1, int v2) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 20 * 1000; i++) {
                builder.append("a");
            }
            throw new RuntimeException(builder.toString());
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> alwaysThrow(-234, 23));
            SafeCaller.call(() -> alwaysThrowWithLargeMessage(-234, 23));
        }
    }

    @Test
    public void shouldPrintRuntimeException() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(TestCases.class).setMethodToRecord("alwaysThrow")
        );

        assertThat(root.hasThrown(), is(true));

        ObjectRepresentation thrown = root.getReturnValue();

        assertThat(thrown, instanceOf(PlainObjectRepresentation.class));
        assertThat(thrown.getPrintedText(), containsString("asdasdaghdajd"));
    }

    @Test
    public void shouldCutExceptionMessage() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(TestCases.class).setMethodToRecord("alwaysThrowWithLargeMessage")
        );

        assertThat(root.hasThrown(), is(true));

        ObjectRepresentation thrown = root.getReturnValue();

        assertThat(thrown, instanceOf(PlainObjectRepresentation.class));
        System.out.println(thrown.getPrintedText());
        assertThat(thrown.getPrintedText().length(), lessThan(1000));
    }
}
