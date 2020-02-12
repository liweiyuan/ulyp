package com.ulyp.ui.util;

import java.util.HashSet;
import java.util.Set;

public class HashSetIndex implements SearchIndex {

    private final Set<String> values;

    public HashSetIndex(Set<String> values) {
        this.values = values;
    }

    @Override
    public Set<String> values() {
        return values;
    }

    @Override
    public boolean contains(String strToSearch) {
        boolean containsRaw = values.contains(strToSearch);
        if (containsRaw) {
            return true;
        } else {
            return values.stream().anyMatch(v -> v != null && v.contains(strToSearch));
        }
    }

    @Override
    public SearchIndex mergeWith(SearchIndex other) {
        values.addAll(other.values());
        return this;
    }

    public static SearchIndex empty() {
        return new HashSetIndex(new HashSet<>());
    }
}
