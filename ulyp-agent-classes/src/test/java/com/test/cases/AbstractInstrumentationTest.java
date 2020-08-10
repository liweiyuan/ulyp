package com.test.cases;

import com.test.cases.util.*;
import com.ulyp.core.CallEnterTraceList;
import com.ulyp.core.CallExitTraceList;
import com.ulyp.core.ClassDescriptionList;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.CallTraceTree;
import com.ulyp.core.CallGraphDatabase;
import com.ulyp.core.CallGraphDao;
import com.ulyp.core.heap.HeapCallGraphDatabase;
import com.ulyp.transport.TCallTraceLogUploadRequest;
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
        int port = TestUtil.pickEmptyPort();
        try (UIServerStub stub = new UIServerStub(settings, port)) {
            TestUtil.runClassInSeparateJavaProcess(settings.setPort(port));

            TCallTraceLogUploadRequest request = stub.get(1, TimeUnit.MINUTES);
            Assert.assertNotNull(request);

            CallGraphDatabase database = new HeapCallGraphDatabase();
            return new CallGraphDao(
                    new CallEnterTraceList(request.getTraceLog().getEnterTraces()),
                    new CallExitTraceList(request.getTraceLog().getExitTraces()),
                    new MethodDescriptionList(request.getMethodDescriptionList().getData()),
                    new ClassDescriptionList(request.getClassDescriptionList().getData()),
                    database
            ).getCallTraceTree();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
            return null; // won't happen
        }
    }
}