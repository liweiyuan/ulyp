package com.ulyp.agent;

import com.ulyp.agent.tests.SeveralMethodsCases;
import com.ulyp.agent.tests.SimpleCases;
import com.ulyp.agent.transport.MethodTraceTree;
import com.ulyp.agent.transport.MethodTraceTreeBuilder;
import com.ulyp.agent.transport.MethodTraceTreeNode;
import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class InstrumentationCodeTest extends InstrumentationTest {

    @Test
    public void shouldReturnValidMainClassName() {
        TMethodTraceLogUploadRequest request = executeClass(SimpleCases.class, "com.ulyp.agent.tests", "SimpleCases.returnIntWithEmptyParams");

        Assert.assertEquals(SimpleCases.class.getName(), request.getMainClassName());
    }

    @Test
    public void shouldProvideValidTracesForSimpleMethod() {
        MethodTraceTree tree = MethodTraceTreeBuilder.from(executeClass(
                SimpleCases.class,
                "com.ulyp.agent.tests",
                "SimpleCases.returnIntWithEmptyParams"
        ));

        MethodTraceTreeNode root = tree.getRoot();
        assertThat(root.getChildren(), Matchers.empty());
//        assertThat();
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
