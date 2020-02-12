package com.ulyp.agent.util;

import java.util.function.Supplier;

public class NoopLog implements Log {

    @Override
    public void log(Supplier<String> supplier) {

    }
}
