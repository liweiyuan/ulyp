package com.ulyp.agent.transport;

import java.io.IOException;

public interface UiAddress {

    UiTransport buildTransport() throws IOException;
}
