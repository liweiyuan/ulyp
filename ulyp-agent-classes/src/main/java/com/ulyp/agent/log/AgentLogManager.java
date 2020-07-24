package com.ulyp.agent.log;

import org.apache.logging.log4j.Logger;

public class AgentLogManager {

    public static Logger getLogger(final Class<?> clazz) {
        if (!LoggingSettings.IS_LOGGING_TURNED_ON) {
            return EmptyLogger.getInstance();
        } else {
            return LoggingSettings.ctx.getLogger(clazz.getName());
        }
    }
}
