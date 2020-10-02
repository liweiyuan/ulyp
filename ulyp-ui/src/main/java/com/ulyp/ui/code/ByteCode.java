package com.ulyp.ui.code;

public class ByteCode {

    private final String className;
    private final byte[] bytecode;

    public ByteCode(String className, byte[] bytecode) {
        this.className = className;
        this.bytecode = bytecode;
    }

    SourceCode decompile() {
        return null;
    }
}
