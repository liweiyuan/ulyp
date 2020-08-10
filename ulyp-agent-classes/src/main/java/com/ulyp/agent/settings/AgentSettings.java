package com.ulyp.agent.settings;

import java.util.List;

public interface AgentSettings {

    int getMaxTreeDepth();

    int getMinTraceCount();

    int getMaxCallsPerMethod();

    List<String> getInstrumentatedPackages();

    List<String> getExcludedFromInstrumentationPackages();
}
