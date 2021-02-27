package com.ulyp.agent.transport.file2;

import com.ulyp.agent.transport.CallRecordTreeRequest;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.database.DatabaseWriterImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Dumps all requests to file which can later be opened in UI
 */
public class FileUiTransport implements UiTransport {

    private final DatabaseWriterImpl writer;

    public FileUiTransport(Path filePath) throws IOException {
        writer = new DatabaseWriterImpl(filePath);
    }

    public void uploadAsync(CallRecordTreeRequest request) {
        try {
            writer.write(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdownNowAndAwaitForRecordsLogsSending(long time, TimeUnit timeUnit) throws InterruptedException {
        /*
        for (Future<?> future : this.convertingFutures) {
            try {
                future.get(time, timeUnit);
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
                return;
            }
        }

        fileWriterTask.shutdownAndWaitForTasksToComplete(time, timeUnit);
        */
    }
}
