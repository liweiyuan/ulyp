package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
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
    public ObjectRepresentation read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        return new NumberObject(classDescription, binaryInput.readString());
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.write(obj.toString());
    }
}
