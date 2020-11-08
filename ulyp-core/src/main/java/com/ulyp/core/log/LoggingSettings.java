package com.ulyp.core.log;

@SuppressWarnings("all")
public class LoggingSettings {

    public static final boolean IS_DEBUG_TURNED_ON;
    public static final boolean IS_TRACE_TURNED_ON;
    public static final boolean IS_LOGGING_TURNED_ON;

    public static final String LOG_LEVEL_PROPERTY = "ulyp.log-level";
    public static final LogLevel LOG_LEVEL = LogLevel.valueOf(System.getProperty(LOG_LEVEL_PROPERTY, LogLevel.INFO.name()));

    static {
        IS_DEBUG_TURNED_ON = LOG_LEVEL == LogLevel.DEBUG || LOG_LEVEL == LogLevel.TRACE;
        IS_TRACE_TURNED_ON = LOG_LEVEL == LogLevel.TRACE;
        IS_LOGGING_TURNED_ON = IS_DEBUG_TURNED_ON || IS_TRACE_TURNED_ON;
    }
}
