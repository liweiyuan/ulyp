package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class NumbersPrinter extends ObjectBinaryPrinter {

    protected NumbersPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isPrimitive() || type.isBoxedNumber();
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.write(obj.toString());
    }
}
