package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.StringView;

public class NullObjectPrinter extends ObjectBinaryPrinter {

    private StringView NULL_STRING = new StringView("null");

    protected NullObjectPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return false;
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        // still need to read as this printer may be used inside another printer
        binaryInput.readLong();
        return NULL_STRING;
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.write(0);
    }
}
