package com.ulyp.database;

import com.ulyp.agent.transport.CallRecordTreeRequest;
import com.ulyp.core.*;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.TypeInfo;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class DatabaseWriterImpl implements Database.Writer {

    private final Path path;
    private final EnhancedRandomAccessFile randomAccessFile;
    private final WithSizeOutputStream outputStream;
    private int count = 0;

    private final CallEnterRecordList enterRecords = new CallEnterRecordList();
    private final CallExitRecordList exitRecords = new CallExitRecordList();

    public DatabaseWriterImpl(Path path) throws IOException {
        this.path = path;
        this.outputStream = new WithSizeOutputStream(new BufferedOutputStream(new FileOutputStream(path.toFile(), false)));
        this.randomAccessFile = new EnhancedRandomAccessFile(new RandomAccessFile(path.toFile(), "w"));
    }

    @Override
    public void writeEnterRecord(
            long recordingSessionId,
            long callId,
            int methodId,
            AgentRuntime agentRuntime,
            ObjectBinaryPrinter[] printers,
            Object callee,
            Object[] args)
    {

    }

    @Override
    public void writeExitRecord(
            long recordingSessionId,
            long callId,
            int methodId,
            AgentRuntime agentRuntime,
            boolean thrown,
            ObjectBinaryPrinter returnValuePrinter,
            Object returnValue)
    {

    }

    @Override
    public void write(CallRecordTreeRequest request) throws IOException {

        ClassDescriptionList classDescriptionList = new ClassDescriptionList();
        for (TypeInfo typeInfo : request.getTypes()) {
            classDescriptionList.add(typeInfo);
        }

        classDescriptionList.writeTo(outputStream);

        MethodInfoList methodInfoList = new MethodInfoList();
        for (MethodInfo description : request.getMethods()) {
            methodInfoList.add(description);
        }

        methodInfoList.writeTo(outputStream);

        incrementCount();
    }

    @Override
    public void close() {

    }

    @Override
    public void shutdownNowAndAwaitForRecordsLogsSending(int i, TimeUnit seconds) {

    }

    private void incrementCount() throws IOException {
        count++;
        randomAccessFile.writeIntAt(0, count++);
    }
}
