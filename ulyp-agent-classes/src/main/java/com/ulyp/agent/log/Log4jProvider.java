package com.ulyp.agent.log;

import org.apache.logging.log4j.spi.Provider;

public class Log4jProvider extends Provider {
    public Log4jProvider() {
        super(10, "2.6.0", Log4jContextFactory.class);
    }
}
