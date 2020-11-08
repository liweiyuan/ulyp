package com.ulyp.core.log;

public class EmptyLogger implements Logger {

    @Override
    public void info(String msg) {

    }

    @Override
    public void debug(String msg) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }
}
