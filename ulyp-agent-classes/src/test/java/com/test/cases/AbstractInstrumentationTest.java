package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.test.cases.util.TestUtil;
import com.test.cases.util.UIServerStub;
import com.ulyp.core.CallEnterRecordList;
import com.ulyp.core.CallExitRecordList;
import com.ulyp.core.CallRecord;
import com.ulyp.core.MethodInfoList;
import com.ulyp.core.impl.OnDiskFileBasedCallRecordDatabase;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.io.IOException;
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
        List<TCallRecordLogUploadRequest> requests = runSubprocessWithUiAndReturnProtoRequest(settings);

        OnDiskFileBasedCallRecordDatabase database = new OnDiskFileBasedCallRecordDatabase();

        for (TCallRecordLogUploadRequest request : requests) {
            try {
                database.persistBatch(
                        new CallEnterRecordList(request.getRecordLog().getEnterRecords()),
                        new CallExitRecordList(request.getRecordLog().getExitRecords()),
                        new MethodInfoList(request.getMethodDescriptionList().getData()),
                        request.getDescriptionList()
                );
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }

        return database.getRoot();
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