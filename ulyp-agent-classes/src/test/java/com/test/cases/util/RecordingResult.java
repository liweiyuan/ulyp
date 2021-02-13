package com.test.cases.util;

import com.ulyp.core.*;
import com.ulyp.core.impl.InMemoryIndexFileBasedCallRecordDatabase;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordingResult {

    private final List<TCallRecordLogUploadRequest> requests;
    private final Map<Long, CallRecordDatabase> recordingIdToRequest = new HashMap<>();

    public RecordingResult(List<TCallRecordLogUploadRequest> requests) {
        this.requests = requests;

        for (TCallRecordLogUploadRequest request : requests) {
            CallRecordDatabase database = recordingIdToRequest.computeIfAbsent(
                    request.getRecordingInfo().getRecordingId(),
                    id -> new InMemoryIndexFileBasedCallRecordDatabase()
            );

            database.persistBatch(
                    new CallEnterRecordList(request.getRecordLog().getEnterRecords()),
                    new CallExitRecordList(request.getRecordLog().getExitRecords()),
                    new MethodInfoList(request.getMethodDescriptionList().getData()),
                    request.getDescriptionList()
            );
        }
    }

    public CallRecord getSingleRoot() {
        assertSingleRecordingSession();
        return recordingIdToRequest.entrySet().iterator().next().getValue().getRoot();
    }

    public void assertSingleRecordingSession() {
        Assert.assertEquals("Expect single recording session, but got " + recordingIdToRequest.size(), 1, requests.size());
    }

    public void assertRecordingSessionCount(int count) {
        Assert.assertEquals("Expect " + count + " recording session, but got " + recordingIdToRequest.size(), count, requests.size());
    }
}
