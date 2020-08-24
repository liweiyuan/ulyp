package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.test.cases.util.TestUtil;
import com.test.cases.util.UIServerStub;
import com.ulyp.core.*;
import com.ulyp.core.impl.HeapCallRecordDatabase;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AbstractInstrumentationTest {

    protected void runSubprocessAndExpectNotConnected(TestSettingsBuilder settings) {
        settings.setUiEnabled(false);

        int port = TestUtil.pickEmptyPort();
        try (UIServerStub stub = new UIServerStub(settings, port)) {
            TestUtil.runClassInSeparateJavaProcess(settings.setPort(port));

            try {
                stub.get(5, TimeUnit.SECONDS);

                Assert.fail("Got record but expected that subprocess will not connect");
            } catch (TimeoutException te) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not record log: " + e.getMessage());
        }
    }

    @NotNull
    protected CallRecord runSubprocessWithUi(TestSettingsBuilder settings) {
        TCallRecordLogUploadRequest request = runSubprocessWithUiAndReturnRecordLogRaw(settings);

        CallRecordDatabase database = new HeapCallRecordDatabase();
        return new CallRecordTreeDao(
                new CallEnterRecordList(request.getRecordLog().getEnterRecords()),
                new CallExitRecordList(request.getRecordLog().getExitRecords()),
                new MethodInfoList(request.getMethodDescriptionList().getData()),
                request.getDescriptionList(),
                database
        ).get();
    }

    @NotNull
    protected TCallRecordLogUploadRequest runSubprocessWithUiAndReturnRecordLogRaw(TestSettingsBuilder settings) {
        int port = TestUtil.pickEmptyPort();
        try (UIServerStub stub = new UIServerStub(settings, port)) {
            TestUtil.runClassInSeparateJavaProcess(settings.setPort(port));

            return stub.get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
            return null; // won't happen
        }
    }
}