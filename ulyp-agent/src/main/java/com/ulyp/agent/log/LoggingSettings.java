package com.ulyp.agent.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

@SuppressWarnings("all")
public class LoggingSettings {

    public static final boolean IS_DEBUG_TURNED_ON;
    public static final boolean IS_TRACE_TURNED_ON;
    public static final boolean IS_LOGGING_TURNED_ON;

    public static final String LOG_LEVEL_PROPERTY = "ulyp.log-level";
    public static final LoggerContext ctx;
    public static final Level LOG_LEVEL = Level.valueOf(System.getProperty(LOG_LEVEL_PROPERTY, Level.OFF.name()));

    static {
        IS_DEBUG_TURNED_ON = LOG_LEVEL == Level.DEBUG || LOG_LEVEL == Level.TRACE;
        IS_TRACE_TURNED_ON = LOG_LEVEL == Level.TRACE;
        IS_LOGGING_TURNED_ON = IS_DEBUG_TURNED_ON || IS_TRACE_TURNED_ON;

        if (LOG_LEVEL != Level.OFF) {
            ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
            AppenderComponentBuilder console = builder.newAppender("stdout", "Console");
            builder.add(console);

            LayoutComponentBuilder standard = builder.newLayout("PatternLayout");
            standard.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");
            console.add(standard);

            LoggerComponentBuilder logger = builder.newLogger("com.ulyp", LOG_LEVEL);
            logger.add(builder.newAppenderRef("stdout"));
            logger.addAttribute("additivity", false);
            builder.add(logger);

            ctx = Configurator.initialize(builder.build());
        } else {
            ctx = null;
        }
    }
}
