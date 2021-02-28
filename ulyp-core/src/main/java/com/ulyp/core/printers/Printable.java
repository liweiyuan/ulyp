package com.ulyp.core.printers;

// TODO retire
public interface Printable {

    default String getPrintedText() {
        return print();
    }

    // TODO can be retired
    default String print() {
        return "";
    }
}
