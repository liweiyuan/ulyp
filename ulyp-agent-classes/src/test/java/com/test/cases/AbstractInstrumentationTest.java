package com.test.cases;

import com.test.cases.util.*;
import com.ulyp.core.CallEnterRecordList;
import com.ulyp.core.CallExitRecordList;
import com.ulyp.core.ClassDescriptionList;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.CallTraceTree;
import com.ulyp.core.CallGraphDatabase;
import com.ulyp.core.CallGraphDao;
import com.ulyp.core.heap.HeapCallGraphDatabase;
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

                Assert.fail("Got trace but expected that subprocess will not connect");
            } catch (TimeoutException te) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
        }
    }

    @NotNull
    protected CallTraceTree runSubprocessWithUi(TestSettingsBuilder settings) {
        TCallRecordLogUploadRequest request = runSubprocessWithUiAndReturnTraceLogRaw(settings);

        CallGraphDatabase database = new HeapCallGraphDatabase();
        return new CallGraphDao(
                new CallEnterRecordList(request.getTraceLog().getEnterTraces()),
                new CallExitRecordList(request.getTraceLog().getExitTraces()),
                new MethodDescriptionList(request.getMethodDescriptionList().getData()),
                new ClassDescriptionList(request.getClassDescriptionList().getData()),
                database
        ).getCallTraceTree();
    }

    @NotNull
    protected TCallRecordLogUploadRequest runSubprocessWithUiAndReturnTraceLogRaw(TestSettingsBuilder settings) {
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