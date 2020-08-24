package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class EnumPrinter extends ObjectBinaryPrinter {

    protected EnumPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        return typeInfo.isEnum();
    }

    @Override
    public void write(Object obj, TypeInfo typeInfo, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.writeString(((Enum<?>) obj).name());
    }
}
