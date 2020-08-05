package com.ulyp.agent.settings;

import com.ulyp.agent.transport.UiAddress;
import java.util.List;

public interface AgentSettings {

    UiAddress getUiAddress();

    int getMaxTreeDepth();

    int getMinTraceCount();

    int getMaxCallsPerMethod();

    List<String> getInstrumentatedPackages();

    List<String> getExcludedFromInstrumentationPackages();
}
