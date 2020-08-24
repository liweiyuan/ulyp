package com.ulyp.agent.transport;

import com.ulyp.transport.Settings;

/**
 * Only used when UI is disabled explicitly. Users will probably never disable UI, mostly this is used
 * in benchmarks only
 */
public class DisconnectedUiAddress implements UiAddress {

    private final Settings settings;

    public DisconnectedUiAddress(Settings settings) {
        this.settings = settings;
    }

    @Override
    public UiTransport buildTransport() {
        return new DisconnectedUiTransport(settings);
    }
}
