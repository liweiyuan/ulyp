package com.ulyp.core.printers;

import java.util.List;

public class MapRepresentation extends ObjectRepresentation {

    private final int size;
    private final List<MapEntryRepresentation> entries;

    // Not all elements are recorded, therefore objectsRepresentations.size() != length
    protected MapRepresentation(TypeInfo typeInfo, int size, List<MapEntryRepresentation> entries) {
        super(typeInfo);

        this.size = size;
        this.entries = entries;
    }

    public int getSize() {
        return size;
    }

    public List<MapEntryRepresentation> getEntries() {
        return entries;
    }
}
