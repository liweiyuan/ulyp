package com.ulyp.agent.transport;

import com.ulyp.transport.SettingsResponse;

/**
 * Only used when UI is disabled explicitly. Users will probably never disable UI, mostly this is used
 * in benchmarks only
 */
public class DisconnectedUiAddress implements UiAddress {

    private final SettingsResponse settingsResponse;

    public DisconnectedUiAddress(SettingsResponse settingsResponse) {
        this.settingsResponse = settingsResponse;
    }

    @Override
    public UiTransport buildTransport() {
        return new DisconnectedUiTransport(settingsResponse);
    }
}
