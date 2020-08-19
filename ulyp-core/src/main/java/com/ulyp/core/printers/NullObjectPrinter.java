package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class NullObjectPrinter extends ObjectBinaryPrinter {

    protected NullObjectPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return false;
    }

    @Override
    public ObjectRepresentation read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        // still need to read as this printer may be used inside another printer
        binaryInput.readLong();
        return NullObject.getInstance();
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.write(0);
    }
}
