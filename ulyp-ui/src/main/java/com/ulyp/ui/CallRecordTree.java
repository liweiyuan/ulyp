package com.ulyp.ui;

import com.ulyp.core.*;
import com.ulyp.core.impl.HeapCallRecordDatabase;
import com.ulyp.transport.ProcessInfo;
import com.ulyp.transport.TCallRecordLog;
import com.ulyp.transport.TCallRecordLogUploadRequest;

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
    private final Duration lifetime;

    private final CallRecordDatabase database = new HeapCallRecordDatabase();

    public CallRecordTree(TCallRecordLogUploadRequest request) {

        TCallRecordLog recordLog = request.getRecordLog();

        this.root = new CallRecordTreeDao(
                new CallEnterRecordList(recordLog.getEnterRecords()),
                new CallExitRecordList(recordLog.getExitRecords()),
                new MethodDescriptionList(request.getMethodDescriptionList().getData()),
                new ClassDescriptionList(request.getClassDescriptionList().getData()),
                database
        ).get();

        this.id = counter.incrementAndGet();
        this.lifetime = Duration.ofMillis(request.getLifetimeMillis());
        this.processInfo = request.getProcessInfo();
        this.threadName = recordLog.getThreadName();
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public String getThreadName() {
        return threadName;
    }

    public long getId() {
        return id;
    }

    public Duration getLifetime() {
        return lifetime;
    }

    public CallRecord getRoot() {
        return root;
    }

    public void dispose() {
        database.deleteSubtree(root.getId());
    }
}
