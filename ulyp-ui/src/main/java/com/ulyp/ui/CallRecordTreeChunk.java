package com.ulyp.ui;

import com.ulyp.core.*;
import com.ulyp.transport.ProcessInfo;
import com.ulyp.transport.RecordingInfo;
import com.ulyp.transport.TCallRecordLogUploadRequest;

/**
 * Full deserialized record log sent from agent formed as a tree.
 * The tree itself might be incomplete
 */
public class CallRecordTreeChunk {

    private final TCallRecordLogUploadRequest request;

    public CallRecordTreeChunk(TCallRecordLogUploadRequest request) {
        this.request = request;
    }

    public CallRecord uploadTo(CallRecordDatabase database) {

        return new CallRecordTreeDeserializer(
                new CallEnterRecordList(request.getRecordLog().getEnterRecords()),
                new CallExitRecordList(request.getRecordLog().getExitRecords()),
                new MethodInfoList(request.getMethodDescriptionList().getData()),
                request.getDescriptionList(),
                database
        ).get();
    }

    public ProcessInfo getProcessInfo() {
        return request.getRecordingInfo().getProcessInfo();
    }

    public RecordingInfo getRecordingInfo() {
        return request.getRecordingInfo();
    }

    public long getRecordingId() {
        return request.getRecordingInfo().getId();
    }

    public void dispose() {
//        database.deleteSubtree(root.getId());
    }
}
