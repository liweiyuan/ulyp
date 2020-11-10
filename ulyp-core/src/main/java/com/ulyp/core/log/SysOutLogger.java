package com.ulyp.core.log;

import java.util.Date;

public class SysOutLogger implements Logger {

    @Override
    public void error(String msg, Exception e) {
        System.out.println(new Date() + " | " + Thread.currentThread().getName() + " | ERROR | " + msg + ", exception msg: " + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void info(String msg) {
        System.out.println(new Date() + " | " + Thread.currentThread().getName() + " | INFO | " + msg);
    }

    @Override
    public void debug(String msg) {
        if (LoggingSettings.IS_DEBUG_TURNED_ON) {
            System.out.println(new Date() + " | " + Thread.currentThread().getName() + " | DEBUG | " + msg);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return LoggingSettings.IS_DEBUG_TURNED_ON;
    }
}
