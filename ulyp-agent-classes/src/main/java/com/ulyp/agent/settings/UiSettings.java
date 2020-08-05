package com.ulyp.agent.settings;

import com.ulyp.agent.transport.NamedThreadFactory;
import com.ulyp.agent.transport.UploadingTransport;
import com.ulyp.transport.SettingsResponse;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class UiSettings {

    private final ScheduledExecutorService settingsUpdatingService = Executors.newScheduledThreadPool(
            1,
            new NamedThreadFactory("Settings-Updater", true)
    );

    private final SettingsProperty<List<String>> instrumentedPackages = new SettingsProperty<>("Instrumented packages");
    private final SettingsProperty<List<String>> excludeFromInstrumentationPackages = new SettingsProperty<>("Exclude from trace packages list");
    private final SettingsProperty<TracingStartMethodList> tracingStartMethod = new SettingsProperty<>("Tracing start methods list");
    private final SettingsProperty<Boolean> mayStartTracing = new SettingsProperty<>("May start tracing", true);
    private final SettingsProperty<Boolean> traceCollections = new SettingsProperty<>("Trace collection", true);

    public UiSettings(UploadingTransport uploadingTransport) {
        try {
            SettingsResponse settings = uploadingTransport.getSettingsBlocking(Duration.ofSeconds(3));
            onSettings(settings);
        } catch (Exception e) {
            // NOP
        }

        settingsUpdatingService.scheduleAtFixedRate(() -> {
            try {
                SettingsResponse settings = uploadingTransport.getSettingsBlocking(Duration.ofMillis(500));
                onSettings(settings);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // NOP
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void onSettings(SettingsResponse settings) {
        mayStartTracing.setValue(settings.getMayStartTracing());
        traceCollections.setValue(settings.getTraceCollections());

        excludeFromInstrumentationPackages.setValue(settings.getExcludedFromInstrumentationPackagesList());
        instrumentedPackages.setValue(settings.getInstrumentedPackagesList());
        tracingStartMethod.setValue(new TracingStartMethodList(settings.getTraceStartMethodsList()));
    }

    public SettingsProperty<List<String>> getExcludeFromInstrumentationPackages() {
        return excludeFromInstrumentationPackages;
    }

    public SettingsProperty<List<String>> getInstrumentedPackages() {
        return instrumentedPackages;
    }

    public SettingsProperty<TracingStartMethodList> getTracingStartMethod() {
        return tracingStartMethod;
    }

    public SettingsProperty<Boolean> mayStartTracing() {
        return mayStartTracing;
    }

    public SettingsProperty<Boolean> traceCollections() {
        return traceCollections;
    }
}
