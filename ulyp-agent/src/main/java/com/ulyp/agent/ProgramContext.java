package com.ulyp.agent;

import com.ulyp.agent.transport.UploadingTransport;
import com.ulyp.agent.util.Log;

public interface ProgramContext {

    Settings getSettings();

    MethodDescriptionDictionary getMethodCache();

    Log getLog();

    String getMainClassName();

    UploadingTransport getTransport();
}
