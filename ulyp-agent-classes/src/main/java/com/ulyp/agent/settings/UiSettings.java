package com.ulyp.agent.settings;

import com.ulyp.agent.transport.NamedThreadFactory;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.core.util.PackageList;
import com.ulyp.transport.Settings;

import java.time.Duration;
import java.util.concurrent.*;

public class UiSettings {

    private final ScheduledExecutorService settingsUpdatingService = Executors.newScheduledThreadPool(
            1,
            new NamedThreadFactory("Settings-Updater", true)
    );

    private final SettingsProperty<PackageList> instrumentedPackages = new SettingsProperty<>("Instrumented packages", new PackageList());
    private final SettingsProperty<PackageList> excludeFromInstrumentationPackages = new SettingsProperty<>("Exclude from instrumentation packages", new PackageList());
    private final SettingsProperty<RecordingStartMethodList> recordingStartMethod = new SettingsProperty<>("Tracing start methods list");
    private final SettingsProperty<Boolean> mayStartRecording = new SettingsProperty<>("May start tracing", true);
    private final SettingsProperty<Boolean> recordCollectionItems = new SettingsProperty<>("Record collection items", true);

    public UiSettings(UiTransport uiTransport) {
        try {
            Settings settings = uiTransport.getSettingsBlocking(Duration.ofSeconds(3));
            onSettings(settings);
        } catch (Exception e) {
            // NOP
        }

        settingsUpdatingService.scheduleAtFixedRate(() -> {
            try {
                Settings settings = uiTransport.getSettingsBlocking(Duration.ofMillis(500));
                onSettings(settings);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // NOP
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void onSettings(Settings settings) {
        mayStartRecording.setValue(settings.getMayStartRecording());
        recordCollectionItems.setValue(settings.getRecordCollectionsItems());

        excludeFromInstrumentationPackages.setValue(new PackageList(settings.getExcludedFromInstrumentationPackagesList()));
        instrumentedPackages.setValue(new PackageList(settings.getInstrumentedPackagesList()));
        recordingStartMethod.setValue(new RecordingStartMethodList(settings.getMethodsToRecordList()));

    }

    public SettingsProperty<PackageList> getExcludeFromInstrumentationPackages() {
        return excludeFromInstrumentationPackages;
    }

    public SettingsProperty<PackageList> getInstrumentedPackages() {
        return instrumentedPackages;
    }

    public SettingsProperty<RecordingStartMethodList> getRecordingStartMethod() {
        return recordingStartMethod;
    }

    public SettingsProperty<Boolean> mayStartTracing() {
        return mayStartRecording;
    }

    public SettingsProperty<Boolean> getRecordCollectionItems() {
        return recordCollectionItems;
    }
}
