package com.test.cases;

import com.test.cases.util.*;
import com.ulyp.core.CallRecord;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.Comparator;
import java.util.List;

public class AbstractInstrumentationTest {

    protected void runSubprocessAndExpectNotConnected(TestSettingsBuilder settings) {
        if (settings.getOutputFile() != null) {
            settings.setOutputFile(new OutputFile("test", ".dat"));
        }

        TestUtil.runClassInSeparateJavaProcess(settings);

        List<TCallRecordLogUploadRequest> requests = settings.getOutputFile().read();
        Assert.assertEquals(requests.size(), 0);
    }

    @NotNull
    protected CallRecord runSubprocessWithUi(TestSettingsBuilder settings) {
        return new RecordingResult(runSubprocessWithUiAndReturnProtoRequest(settings)).getSingleRoot();
    }

    @NotNull
    protected RecordingResult runSubprocess(TestSettingsBuilder settings) {
        return new RecordingResult(runSubprocessWithUiAndReturnProtoRequest(settings));
    }

    protected List<TCallRecordLogUploadRequest> runSubprocessWithUiAndReturnProtoRequest(TestSettingsBuilder settings) {
        if (settings.getOutputFile() != null) {
            settings.setOutputFile(new OutputFile("test", ".dat"));
        }
        TestUtil.runClassInSeparateJavaProcess(settings);
        List<TCallRecordLogUploadRequest> requests = settings.getOutputFile().read();
        requests.sort(Comparator.comparingLong(r -> r.getRecordingInfo().getChunkId()));
        System.out.println("Got " + requests.size() + " chunks from process");
        return requests;
    }
}