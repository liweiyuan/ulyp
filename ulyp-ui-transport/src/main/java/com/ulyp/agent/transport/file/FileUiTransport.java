package com.ulyp.agent.transport.file;

import com.ulyp.agent.transport.CallRecordTreeRequest;
import com.ulyp.agent.transport.NamedThreadFactory;
import com.ulyp.agent.transport.RequestConverter;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.core.*;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.transport.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Dumps all requests to file which can later be opened in UI
 */
public class FileUiTransport implements UiTransport {

    private final Settings settings;
    private final Path filePath;
    private final Set<Future<?>> convertingFutures = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final ExecutorService executorService = Executors.newFixedThreadPool(
            5,
            new NamedThreadFactory("Record-Log-Converter", true)
    );

    private TCallRecordLogUploadRequestList.Builder requestList = TCallRecordLogUploadRequestList.newBuilder();

    public FileUiTransport(Settings settings, Path filePath) {
        this.filePath = filePath;
        this.settings = settings;
    }

    public Settings getSettingsBlocking(Duration duration) {
        return settings;
    }

    public void uploadAsync(CallRecordTreeRequest request) {

        convertingFutures.add(executorService.submit(() -> addToList(request)));
    }

    private void addToList(CallRecordTreeRequest request) {
        TCallRecordLogUploadRequest protoRequest = RequestConverter.convert(request);

        synchronized (this) {
            requestList.addRequest(protoRequest);
        }
    }

    public void shutdownNowAndAwaitForRecordsLogsSending(long time, TimeUnit timeUnit) throws InterruptedException {
        for (Future<?> future : this.convertingFutures) {
            try {
                future.get(time, timeUnit);
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
                return;
            }
        }

        synchronized (this) {

            TCallRecordLogUploadRequestList requests = requestList.build();

            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath.toFile(), false))) {
                requests.writeTo(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
