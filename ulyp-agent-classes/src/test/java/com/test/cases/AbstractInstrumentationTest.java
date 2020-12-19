package com.test.cases;

import com.test.cases.util.RecordingResult;
import com.test.cases.util.TestSettingsBuilder;
import com.test.cases.util.TestUtil;
import com.test.cases.util.UIServerStub;
import com.ulyp.core.CallRecord;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.Comparator;
import java.util.List;

public class AbstractInstrumentationTest {

    protected void runSubprocessAndExpectNotConnected(TestSettingsBuilder settings) {
        settings.setUiEnabled(false);

        int port = TestUtil.pickEmptyPort();
        try (UIServerStub stub = new UIServerStub(settings, port)) {
            TestUtil.runClassInSeparateJavaProcess(settings.setPort(port));

            List<TCallRecordLogUploadRequest> requests = stub.getRequests();

            Assert.assertEquals(requests.size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not record log: " + e.getMessage());
        }
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
        int port = TestUtil.pickEmptyPort();
        try (UIServerStub stub = new UIServerStub(settings, port)) {
            TestUtil.runClassInSeparateJavaProcess(settings.setPort(port));

            List<TCallRecordLogUploadRequest> requests = stub.getRequests();

            requests.sort(Comparator.comparingLong(r -> r.getRecordingInfo().getChunkId()));
            System.out.println("Got " + requests.size() + " chunks from process");
            return requests;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not capture trace log: " + e.getMessage());
            return null; // won't happen
        }
    }
}