package com.ulyp.ui;

import com.ulyp.core.*;
import com.ulyp.core.impl.HeapCallRecordDatabase;
import com.ulyp.transport.ProcessInfo;
import com.ulyp.transport.TCallRecordLog;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import javafx.scene.control.Tooltip;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Full deserialized record log sent from agent
 */
public class CallRecordTree {

    private static final AtomicLong counter = new AtomicLong();

    private final long id;
    private final CallRecord root;
    private final ProcessInfo processInfo;
    private final String threadName;
    private final long epochCreatedTimeMillis;
    private final Duration lifetime;

    private final CallRecordDatabase database = new HeapCallRecordDatabase();

    public CallRecordTree(TCallRecordLogUploadRequest request) {

        TCallRecordLog recordLog = request.getRecordLog();

        this.root = new CallRecordTreeDao(
                new CallEnterRecordList(recordLog.getEnterRecords()),
                new CallExitRecordList(recordLog.getExitRecords()),
                new MethodInfoList(request.getMethodDescriptionList().getData()),
                request.getDescriptionList(),
                database
        ).get();

        this.id = counter.incrementAndGet();
        this.epochCreatedTimeMillis = request.getCreateEpochMillis();
        this.lifetime = Duration.ofMillis(request.getLifetimeMillis());
        this.processInfo = request.getProcessInfo();
        this.threadName = recordLog.getThreadName();
    }

    public String getTabName() {
        return root.getMethodName() + "(" + id + ", life=" + lifetime.toMillis() + " ms, nodes=" + root.getSubtreeNodeCount() + ")";
    }

    public Tooltip getTooltip() {

        String text = "Thread: " + this.threadName + "\n" +
                "Created at: " + new Timestamp(this.epochCreatedTimeMillis) + "\n" +
                "Finished at: " + new Timestamp(this.epochCreatedTimeMillis + lifetime.toMillis()) + "\n" +
                "Lifetime: " + lifetime.toMillis() + " millis" + "\n";

        return new Tooltip(text);
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public CallRecord getRoot() {
        return root;
    }

    public void dispose() {
        database.deleteSubtree(root.getId());
    }
}
