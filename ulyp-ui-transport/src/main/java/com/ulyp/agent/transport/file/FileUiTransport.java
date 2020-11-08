package com.ulyp.agent.transport.file;

import com.ulyp.agent.transport.CallRecordTreeRequest;
import com.ulyp.agent.transport.NamedThreadFactory;
import com.ulyp.agent.transport.RequestConverter;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.transport.Settings;

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
    private final FileWriterTask fileWriterTask;
    private final Set<Future<?>> convertingFutures = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // TODO should be key hashing executor service with 5 threads
    private final ExecutorService convertingExecutorService = Executors.newFixedThreadPool(
            1,
            new NamedThreadFactory("ULYP-Record-Log-Converter", true)
    );

    private final ExecutorService fileWriterService = Executors.newFixedThreadPool(
            1,
            new NamedThreadFactory("ULYP-File-Writer", true)
    );

    public FileUiTransport(Settings settings, Path filePath) {
        this.fileWriterTask = new FileWriterTask(filePath);
        this.fileWriterService.submit(fileWriterTask);
        this.settings = settings;
    }

    public Settings getSettingsBlocking(Duration duration) {
        return settings;
    }

    public void uploadAsync(CallRecordTreeRequest request) {
        convertingFutures.add(convertingExecutorService.submit(() -> {
            fileWriterTask.addToQueue(RequestConverter.convert(request));
        }));
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

        fileWriterTask.shutdownAndWaitForTasksToComplete(time, timeUnit);
    }
}
