package com.test.serialization;

import com.test.cases.AbstractInstrumentationTest;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.junit.Assert;
import org.junit.Test;

public class MethodDescriptionSerializationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldMinimizeAmountMethodDescriptions() {

        TCallRecordLogUploadRequest request = runSubprocessWithUiAndReturnRecordLogRaw(
                new TestSettingsBuilder()
                        .setMainClassName(X.class)
                        .setMethodToRecord(MethodMatcher.parse("X.main"))
        );

        MethodDescriptionList methodDescriptions = new MethodDescriptionList(request.getMethodDescriptionList().getData());

        // Currently there is an overhead of second method description creation for exit advice
        Assert.assertEquals(2, methodDescriptions.size());
    }

    static class X {
        public static void main(String[] args) {
        }
    }
}
