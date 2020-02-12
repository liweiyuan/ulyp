package com.ulyp.agent.vars;

public class PrintersSupport {

    public static final String[] empty = new String[0];

    public static String[] print(Printer[] printers, Object[] params) {
        if (printers.length == 0 && params.length == 0) {
            return empty;
        }
        if(printers.length != params.length) {
            throw new IllegalArgumentException("Printers count is not equal to params count");
        }

        String[] result = new String[printers.length];
        for (int i = 0; i < printers.length; i++) {
            result[i] = printers[i].print(params[i]);
        }
        return result;
    }
}
