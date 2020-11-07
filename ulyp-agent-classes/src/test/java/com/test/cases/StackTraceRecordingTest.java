package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import com.ulyp.transport.TStackTrace;
import com.ulyp.transport.TStackTraceElement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StackTraceRecordingTest extends AbstractInstrumentationTest {

    @Test
    public void shouldRecordStackTraceOfMethodWhenRecordingStarted() {
        List<TCallRecordLogUploadRequest> requests = runSubprocessWithUiAndReturnProtoRequest(
                new TestSettingsBuilder()
                        .setMainClassName(StackTraceTestCase.class)
                        .setMethodToRecord(MethodMatcher.parse("X.foo"))
        );

        TStackTrace stackTrace = requests.get(0).getRecordingInfo().getStackTrace();

        TStackTraceElement firstElement = stackTrace.getElementList().get(0);

        Assert.assertEquals(firstElement.getDeclaringClass(), "com.test.cases.StackTraceRecordingTest$X");
        Assert.assertEquals(firstElement.getMethodName(), "foo");
        Assert.assertEquals(firstElement.getFileName(), "StackTraceRecordingTest.java");

        TStackTraceElement secondElement = stackTrace.getElementList().get(1);

        Assert.assertEquals(secondElement.getDeclaringClass(), "com.test.cases.StackTraceRecordingTest$StackTraceTestCase");
        Assert.assertEquals(secondElement.getMethodName(), "main");
        Assert.assertEquals(secondElement.getFileName(), "StackTraceRecordingTest.java");
    }

    public static class X {

        public void bar() {

        }

        public void foo() {
            bar();
        }
    }

    public static class StackTraceTestCase {

        public static void main(String[] args) {
            new X().foo();
        }
    }
}
