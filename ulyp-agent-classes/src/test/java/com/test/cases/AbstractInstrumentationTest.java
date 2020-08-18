package com.test.cases;

import com.test.cases.util.*;
import com.ulyp.core.CallEnterRecordList;
import com.ulyp.core.CallExitRecordList;
import com.ulyp.core.ClassDescriptionList;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.CallRecordTree;
import com.ulyp.core.CallRecordDatabase;
import com.ulyp.core.CallRecordTreeDao;
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
    protected CallRecordTree runSubprocessWithUi(TestSettingsBuilder settings) {
        TCallRecordLogUploadRequest request = runSubprocessWithUiAndReturnRecordLogRaw(settings);

        CallRecordDatabase database = new HeapCallRecordDatabase();
        return new CallRecordTreeDao(
                new CallEnterRecordList(request.getRecordLog().getEnterRecords()),
                new CallExitRecordList(request.getRecordLog().getExitRecords()),
                new MethodDescriptionList(request.getMethodDescriptionList().getData()),
                new ClassDescriptionList(request.getClassDescriptionList().getData()),
                database
        ).getCallRecordTree();
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