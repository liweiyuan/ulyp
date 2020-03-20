package com.ulyp.agent;

import com.ulyp.core.MethodTraceTree;
import com.ulyp.core.MethodTraceTreeBuilder;
import com.ulyp.agent.util.AgentSettings;
import com.ulyp.agent.util.TestUtil;
import com.ulyp.agent.util.UIServerStub;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.MethodEnterTraceList;
import com.ulyp.core.MethodExitTraceList;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.concurrent.TimeUnit;

public class AbstractInstrumentationTest {

    @NotNull
    protected MethodTraceTree executeClass(AgentSettings settings) {
        int port = TestUtil.pickEmptyPort();
        UIServerStub stub = new UIServerStub(port);

        TestUtil.runClassInSeparateJavaProcess(settings, port);

        try {
            TMethodTraceLogUploadRequest request = stub.get(1, TimeUnit.MINUTES);
            Assert.assertNotNull(request);
            return MethodTraceTreeBuilder.from(
                    new MethodEnterTraceList(request.getTraceLog().getEnterTraces()),
                    new MethodExitTraceList(request.getTraceLog().getExitTraces()),
                    new MethodDescriptionList(request.getMethodDescriptionList().getData())
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
            return null; // won't happen
        }
    }

    @NotNull
    protected MethodTraceTree executeClass(Class<?> cl, String packages, String startMethod) {
        int port = TestUtil.pickEmptyPort();
        UIServerStub stub = new UIServerStub(port);

        TestUtil.runClassInSeparateJavaProcess(cl, packages, startMethod, port);

        try {
            TMethodTraceLogUploadRequest request = stub.get(1, TimeUnit.MINUTES);
            Assert.assertNotNull(request);
            return MethodTraceTreeBuilder.from(
                    new MethodEnterTraceList(request.getTraceLog().getEnterTraces()),
                    new MethodExitTraceList(request.getTraceLog().getExitTraces()),
                    new MethodDescriptionList(request.getMethodDescriptionList().getData())
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
            return null; // won't happen
        }
    }
}