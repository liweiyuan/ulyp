package com.ulyp.agent.util;

import java.util.Optional;

public class ProcessUtils {

    /**
     * @return main class name of this java process or null if it's not possible to define it
     */
    public static Optional<String> getMainClassName() {
        String mainFromProp = System.getProperty("sun.java.command");
        if (mainFromProp != null && !mainFromProp.isEmpty()) {
            int space = mainFromProp.indexOf(' ');
            if (space > 0) {
                return Optional.of(mainFromProp.substring(0, space));
            } else {
                return Optional.of(mainFromProp);
            }
        } else {
            return Optional.empty();
        }
    }
}
