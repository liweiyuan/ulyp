package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.StringView;

public class ThrowablePrinter extends ObjectBinaryPrinter {

    protected ThrowablePrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        // TODO maybe implement
        return false;
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        StringView msg = binaryInput.readString();
        return () -> classDescription.getSimpleName() + ": " + msg;
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        Throwable t = (Throwable) obj;
        out.write(t.getMessage());
    }
}
