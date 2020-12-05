package com.test.printers;

import com.test.cases.AbstractInstrumentationTest;
import com.test.cases.SafeCaller;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.PlainObjectRepresentation;
import com.ulyp.core.printers.StringObjectRepresentation;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class StringTest extends AbstractInstrumentationTest {

    public static class TestCases {

        public static String returnLongString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 20 * 1000; i++) {
                builder.append("a");
            }
            return builder.toString();
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> returnLongString());
        }
    }

    @Test
    public void shouldCutLongStringWhileRecording() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(TestCases.class).setMethodToRecord("returnLongString")
        );

        ObjectRepresentation thrown = root.getReturnValue();
        assertThat(thrown, instanceOf(StringObjectRepresentation.class));
        assertThat(thrown.getPrintedText().length(), lessThan(1000));
    }
}
