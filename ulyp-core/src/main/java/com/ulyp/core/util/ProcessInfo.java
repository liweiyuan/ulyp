package com.ulyp.core.util;

public class ProcessInfo {

    private final String mainClassName;

    public ProcessInfo() {
        this.mainClassName = getMainClassName();

    }

    /**
     * @return main class name of this java process or null if it's not possible to define it
     */
    private static String getMainClassName() {
        String mainFromProp = System.getProperty("sun.java.command");
        if (mainFromProp != null && !mainFromProp.isEmpty()) {
            int space = mainFromProp.indexOf(' ');
            if (space > 0) {
                return mainFromProp.substring(0, space);
            } else {
                return mainFromProp;
            }
        } else {
            return "Unknown";
        }
    }
}
