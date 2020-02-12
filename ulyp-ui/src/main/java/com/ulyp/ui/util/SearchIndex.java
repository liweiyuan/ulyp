package com.ulyp.ui.util;

import java.util.Set;

public interface SearchIndex {

    Set<String> values();

    boolean contains(String strToSearch);

    SearchIndex mergeWith(SearchIndex other);
}
