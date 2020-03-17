package com.ulyp.agent;

import com.ulyp.agent.util.AgentSettings;
import com.ulyp.agent.util.TestUtil;
import com.ulyp.agent.util.UIServerStub;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.concurrent.TimeUnit;

public class AbstractInstrumentationTest {

    @NotNull
    protected TMethodTraceLogUploadRequest executeClass(AgentSettings settings) {
        int port = TestUtil.pickEmptyPort();
        UIServerStub stub = new UIServerStub(port);

        TestUtil.runClassInSeparateJavaProcess(settings, port);

        try {
            TMethodTraceLogUploadRequest request = stub.get(1, TimeUnit.MINUTES);
            Assert.assertNotNull(request);
            return request;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
            return null; // won't happen
        }
    }

    @NotNull
    protected TMethodTraceLogUploadRequest executeClass(Class<?> cl, String packages, String startMethod) {
        int port = TestUtil.pickEmptyPort();
        UIServerStub stub = new UIServerStub(port);

        TestUtil.runClassInSeparateJavaProcess(cl, packages, startMethod, port);

        try {
            TMethodTraceLogUploadRequest request = stub.get(1, TimeUnit.MINUTES);
            Assert.assertNotNull(request);
            return request;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
            return null; // won't happen
        }
    }
}