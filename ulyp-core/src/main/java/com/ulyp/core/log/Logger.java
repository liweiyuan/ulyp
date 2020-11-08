package com.ulyp.core.log;

public interface Logger {

    void info(String msg);

    void debug(String msg);

    boolean isDebugEnabled();
}
