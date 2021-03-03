package com.ulyp.core.printers;

public class MapEntryRepresentation extends ObjectRepresentation {

    private final ObjectRepresentation key;
    private final ObjectRepresentation value;

    protected MapEntryRepresentation(TypeInfo typeInfo, ObjectRepresentation key, ObjectRepresentation value) {
        super(typeInfo);

        this.key = key;
        this.value = value;
    }

    public ObjectRepresentation getKey() {
        return key;
    }

    public ObjectRepresentation getValue() {
        return value;
    }
}
