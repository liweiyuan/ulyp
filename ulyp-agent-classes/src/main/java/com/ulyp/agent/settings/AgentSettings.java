package com.ulyp.agent.settings;

import com.ulyp.core.util.PackageList;

// TODO retire
public interface AgentSettings {

    int getMaxTreeDepth();

    int getMinRecordsCountForLog();

    int getMaxCallsToRecordPerMethod();

    PackageList getInstrumentatedPackages();

    PackageList getExcludedFromInstrumentationPackages();
}
