package com.ulyp.agent.vars;

public class StringPrinter implements Printer {

    public static final Printer instance = new StringPrinter();

    private static final int MAX_LENGTH = 800;

    @Override
    public String print(Object obj) {
        String s = (String) obj;
        if(s == null) {
            return "null";
        } else if (s.length() > MAX_LENGTH) {
            return "'" + s.substring(0, MAX_LENGTH) + "...'(" + s.length() + ")";
        } else {
            return "'" + s + "'";
        }
    }
}
