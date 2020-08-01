package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class EnumPrinter extends ObjectBinaryPrinter {

    protected EnumPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isEnum();
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.write(((Enum<?>) obj).name());
    }
}
