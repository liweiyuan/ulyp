package com.ulyp.agent.util;

public class ProcessUtils {

    /**
     * @return main class name of this java process or null if it's not possible to define it
     */
    public static String getMainClassName() {
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
