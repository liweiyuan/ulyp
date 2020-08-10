package com.ulyp.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Just a list of package matchers, no glob/regex yet
 */
public class PackageList implements Iterable<String> {

    private final List<String> packages;

    public PackageList() {
        this(Collections.emptyList());
    }

    public PackageList(String... packages) {
        this.packages = Arrays.asList(packages);
    }

    public PackageList(List<String> packages) {
        this.packages = packages;
    }

    public boolean isEmpty() {
        return this.packages.isEmpty();
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return packages.iterator();
    }

    @Override
    public String toString() {
        return String.join(",", this.packages);
    }
}
