package com.ulyp.agent.transport.nop;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UiTransport;
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
