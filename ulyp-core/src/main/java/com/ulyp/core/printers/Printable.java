package com.ulyp.core.printers;

@FunctionalInterface
// TODO retire
public interface Printable {

    default String getPrintedText() {
        return print();
    }

    String print();
}
