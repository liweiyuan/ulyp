package com.ulyp.agent.util;

import java.util.function.Supplier;

public class SysoutLog implements Log {

    @Override
    public void log(Supplier<String> supplier) {
        System.out.println("INFO: " + supplier.get());
    }
}
