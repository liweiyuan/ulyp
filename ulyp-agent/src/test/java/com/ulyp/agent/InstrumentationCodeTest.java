package com.ulyp.agent;

import com.ulyp.agent.tests.SeveralMethodsCases;
import com.ulyp.agent.tests.SimpleCases;
import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodInfo;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class InstrumentationCodeTest extends InstrumentationTest {

    @Test
    public void shouldReturnValidMainClassName() {
        TMethodTraceLogUploadRequest request = executeClass(SimpleCases.class, "com.ulyp.agent.tests", "SimpleCases.returnIntWithEmptyParams");

        Assert.assertEquals(SimpleCases.class.getName(), request.getMainClassName());
    }

    @Test
    public void shouldSendMethodInfos() {
        TMethodTraceLogUploadRequest request = executeClass(SimpleCases.class, "com.ulyp.agent.tests", "SimpleCases.returnIntWithEmptyParams");

        List<TMethodInfo> methodInfosList = request.getMethodInfosList();
        TMethodInfo methodInfo = methodInfosList.stream()
                .filter(info -> info.getClassName().equals(SimpleCases.class.getName()) && info.getMethodName().equals("returnIntWithEmptyParams"))
                .findAny()
                .orElseThrow(() -> new AssertionError("Copuld not find method info"));

        Assert.assertEquals(methodInfo.getId(), request.getTraceLog().getEnterTraces(0).getMethodId());
    }

    @Test
    public void shouldProvideValidTracesForSimpleMethod() {
        TMethodTraceLogUploadRequest request = executeClass(SimpleCases.class, "com.ulyp.agent.tests", "SimpleCases.returnIntWithEmptyParams");

        Assert.assertEquals(1, request.getTraceLog().getEnterTracesCount());
        Assert.assertEquals(1, request.getTraceLog().getExitTracesCount());

        TMethodEnterTrace enterTrace = request.getTraceLog().getEnterTraces(0);
        Assert.assertEquals(0, enterTrace.getArgsCount());

        TMethodExitTrace exitTraces = request.getTraceLog().getExitTraces(0);
        Assert.assertEquals("124234232", exitTraces.getReturnValue());
        Assert.assertEquals("", exitTraces.getThrown());
    }

    @Test
    public void shouldBeValidIfMethodThrowsException() {
        TMethodTraceLogUploadRequest request = executeClass(SimpleCases.class, "com.ulyp.agent.tests", "SimpleCases.throwsRuntimeException");

        Assert.assertEquals(1, request.getTraceLog().getEnterTracesCount());
        Assert.assertEquals(1, request.getTraceLog().getExitTracesCount());

        TMethodEnterTrace enterTrace = request.getTraceLog().getEnterTraces(0);
        Assert.assertEquals(0, enterTrace.getArgsCount());

        TMethodExitTrace exitTraces = request.getTraceLog().getExitTraces(0);
        Assert.assertEquals("RuntimeException: 'exception message'", exitTraces.getThrown());
    }

    @Test
    public void shouldBeValidForSeveralMethodCalls() {
        TMethodTraceLogUploadRequest request = executeClass(SeveralMethodsCases.class, "com.ulyp.agent.tests", "SeveralMethodsCases.fib");

        Assert.assertEquals(5, request.getTraceLog().getEnterTracesCount());
        Assert.assertEquals(5, request.getTraceLog().getExitTracesCount());
    }
}
