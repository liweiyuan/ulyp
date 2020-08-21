package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class ClassObjectPrinter extends ObjectBinaryPrinter {

    protected ClassObjectPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        return typeInfo.isClassObject();
    }

    @Override
    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        long typeId = binaryInput.readLong();
        return new PlainObjectRepresentation(typeInfo, "Class{" + decodingContext.getType(typeId).getName() + "}");
    }

    @Override
    public void write(Object obj, TypeInfo typeInfo, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.write(typeInfo.getId());
    }
}
