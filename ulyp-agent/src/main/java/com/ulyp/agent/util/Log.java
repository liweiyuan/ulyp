package com.ulyp.agent.util;

import java.util.function.Supplier;

public interface Log {

    void log(Supplier<String> supplier);
}
