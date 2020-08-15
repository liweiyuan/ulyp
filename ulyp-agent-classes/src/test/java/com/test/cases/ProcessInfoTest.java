package com.test.cases;

import com.google.protobuf.ProtocolStringList;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.transport.ProcessInfo;
import com.ulyp.transport.TCallTraceLogUploadRequest;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProcessInfoTest extends AbstractInstrumentationTest {

    public static class X {

        public static void main(String[] args) {

        }
    }

    @Test
    public void shouldSendValidProcessInfo() {
        TCallTraceLogUploadRequest log = runSubprocessWithUiAndReturnTraceLogRaw(
                new TestSettingsBuilder()
                        .setMainClassName(X.class)
                        .setMethodToTrace("main")
        );

        ProcessInfo processInfo = log.getProcessInfo();

        ProtocolStringList classpath = processInfo.getClasspathList();

        assertThat(classpath.size(), greaterThan(0));

        assertThat(processInfo.getMainClassName(), is("com.test.cases.ProcessInfoTest$X"));
    }
}
