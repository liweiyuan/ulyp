package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public abstract class ObjectBinaryPrinter {

    private final long id;

    protected ObjectBinaryPrinter(int id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public ObjectRepresentation read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        return new PlainObject(classDescription, binaryInput.readString());
    }

    abstract boolean supports(Type type);

    /**
     * @param obj object to print
     * @param out target binary stream to print to
     * @param agentRuntime runtime provided by instrumentation library
     */
    public abstract void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception;
}
