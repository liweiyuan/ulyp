package com.ulyp.ui;

import com.ulyp.core.*;
import com.ulyp.core.impl.HeapCallRecordDatabase;
import com.ulyp.transport.*;
import javafx.scene.control.Tooltip;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Full deserialized record log sent from agent formed as a tree
 */
public class CallRecordTree {

    private static final AtomicLong counter = new AtomicLong();

    private final long id;
    private final CallRecord root;
    private final ProcessInfo processInfo;
    private final TStackTrace stackTrace;
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
        this.stackTrace = recordLog.getStackTrace();
    }

    public String getTabName() {
        return root.getMethodName() + "(" + id + ", life=" + lifetime.toMillis() + " ms, nodes=" + root.getSubtreeNodeCount() + ")";
    }

    public Tooltip getTooltip() {

        StringBuilder builder = new StringBuilder()
                .append("Thread: ").append(this.threadName).append("\n")
                .append("Created at: ").append(new Timestamp(this.epochCreatedTimeMillis)).append("\n")
                .append("Finished at: ").append(new Timestamp(this.epochCreatedTimeMillis + lifetime.toMillis())).append("\n")
                .append("Lifetime: ").append(lifetime.toMillis()).append(" millis").append("\n");

        builder.append("Stack trace: ").append("\n");

        for (TStackTraceElement element: stackTrace.getElementList()) {
            builder.append("\tat ")
                    .append(element.getDeclaringClass())
                    .append(".")
                    .append(element.getMethodName())
                    .append("(")
                    .append(element.getFileName())
                    .append(":")
                    .append(element.getLineNumber())
                    .append(")");
        }

        return new Tooltip(builder.toString());
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
