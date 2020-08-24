package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public abstract class ObjectBinaryPrinter {

    private final byte id;

    protected ObjectBinaryPrinter(byte id) {
        this.id = id;
    }

    public final byte getId() {
        return id;
    }

    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        return new PlainObjectRepresentation(typeInfo, binaryInput.readString());
    }

    abstract boolean supports(TypeInfo typeInfo);

    /**
     * @param obj object to print
     * @param out target binary stream to print to
     * @param agentRuntime runtime provided by instrumentation library
     */
    public abstract void write(Object obj, TypeInfo typeInfo, BinaryOutput out, AgentRuntime agentRuntime) throws Exception;

    /**
     * @param obj object to print
     * @param out target binary stream to print to
     * @param agentRuntime runtime provided by instrumentation library
     */
    // TODO retire this
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        write(obj, agentRuntime.get(obj), out, agentRuntime);
    }
}
