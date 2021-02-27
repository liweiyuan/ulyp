package com.ulyp.database;

import com.ulyp.agent.transport.CallRecordTreeRequest;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.ObjectBinaryPrinter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public interface Database {

    static Writer openForWrite(Path path) throws IOException {
        return new DatabaseWriterImpl(path);
    }

    // not thread safe
    interface Writer extends Closeable {

        void writeEnterRecord(
                long recordingSessionId,
                long callId,
                int methodId,
                AgentRuntime agentRuntime,
                ObjectBinaryPrinter[] printers,
                Object callee,
                Object[] args
        );

        void writeExitRecord(
                long recordingSessionId,
                long callId,
                int methodId,
                AgentRuntime agentRuntime,
                boolean thrown,
                ObjectBinaryPrinter returnValuePrinter,
                Object returnValue
        );

        void write(CallRecordTreeRequest request) throws IOException;

        void close();

        void shutdownNowAndAwaitForRecordsLogsSending(int i, TimeUnit seconds);
    }
}
