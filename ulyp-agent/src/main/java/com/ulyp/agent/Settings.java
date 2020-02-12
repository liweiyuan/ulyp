package com.ulyp.agent;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UploadingTransport;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Settings {

    private UiAddress uiAddress;
    private final List<String> packages;
    private final List<MethodMatcher> methodToStartProfiling;
    private final boolean loggingTurnedOn;

    public Settings(
            UiAddress uiAddress,
            List<String> packages,
            List<MethodMatcher> methodToStartProfiling,
            boolean loggingTurnedOn)
    {
        this.uiAddress = uiAddress;
        this.packages = packages;
        this.methodToStartProfiling = methodToStartProfiling;
        this.loggingTurnedOn = loggingTurnedOn;
    }

    public UiAddress getUiAddress() {
        return uiAddress;
    }

    public boolean loggingTurnedOn() {
        return loggingTurnedOn;
    }

    public static Settings fromJavaProperties() {
        String packagesToInstrument = System.getProperty("ulyp.packages");
        if (packagesToInstrument == null) {
            throw new RuntimeException("Please specify property ulyp.packages");
        }
        List<String> packages = new ArrayList<>(Arrays.asList(packagesToInstrument.split(",")));
        System.out.println("Packages: " + packages);

        String tracedMethods = System.getProperty("ulyp.start-method");
        if (tracedMethods == null) {
            throw new RuntimeException("Please specify property ulyp.start-method");
        }
        List<MethodMatcher> matchers = Arrays.stream(tracedMethods.split(","))
                .map(str -> new MethodMatcher(
                        str.substring(0, str.indexOf('.')),
                        str.substring(str.indexOf('.') + 1))
                ).collect(Collectors.toList());

        // TODO must be removed as not used
        String logArgumentsMethods = System.getProperty("ulyp.method2", "0.0");
        List<MethodMatcher> logArgsExecs = Arrays.stream(logArgumentsMethods.split(","))
                .map(str -> new MethodMatcher(
                        str.substring(0, str.indexOf('.')),
                        str.substring(str.indexOf('.') + 1))
                ).collect(Collectors.toList());

        boolean loggingTurnedOn = System.getProperty("ulyp.log") != null;

        String uiHost = System.getProperty("ulyp.ui-host", UploadingTransport.DEFAULT_ADDRESS.hostName);
        int uiPort = Integer.parseInt(System.getProperty("ulyp.ui-port", String.valueOf(UploadingTransport.DEFAULT_ADDRESS.port)));

        return new Settings(new UiAddress(uiHost, uiPort), packages, matchers, loggingTurnedOn);
    }

    public boolean shouldStartTracing(MethodDescription description) {
        return methodMatches(methodToStartProfiling, description);
    }

    public boolean methodMatches(List<MethodMatcher> methodsToMatch, MethodDescription description) {
        try {

            if (description.isAbstract()) {
                return false;
            }

            boolean profileThis = classMatches(
                    methodsToMatch,
                    description.getDeclaringType().asGenericType(),
                    description.getActualName());
            if (profileThis) {
                return true;
            }

            for (TypeDescription.Generic ctInterface : description.getDeclaringType().asGenericType().getInterfaces()) {
                if (classMatches(methodsToMatch, ctInterface, description.getActualName())) {
                    return true;
                }
            }

            return hasSuperclassThatMatches(
                    methodsToMatch,
                    description.getDeclaringType().getSuperClass(),
                    description.getActualName()
            );
        } catch (Exception e) {
            System.err.println("ERROR while checking if should start profiling: " + e.getMessage());
            return false;
        }
    }

    private boolean hasSuperclassThatMatches(
            List<MethodMatcher> methodsToMatch,
            TypeDescription.Generic clazzType,
            String methodName)
    {
        if(clazzType == null || clazzType.getTypeName().equals("java.lang.Object")) {
            return false;
        }

        return classMatches(methodsToMatch, clazzType, methodName) ||
                hasSuperclassThatMatches(methodsToMatch, clazzType.getSuperClass(), methodName);
    }

    private static class MethodMatcher {
        private final String classSimpleName;
        private final String methodSimpleName;
        private final boolean isWildcard;

        private MethodMatcher(String classSimpleName, String methodSimpleName) {
            this.classSimpleName = classSimpleName;
            this.methodSimpleName = methodSimpleName;
            this.isWildcard = methodSimpleName.equals("*");
        }

        public boolean matchesExact(TypeDescription.Generic clazzType, String methodName) {
            return getSimpleName(clazzType.getActualName()).equals(classSimpleName) &&
                    (isWildcard || methodName.equals(methodSimpleName));
        }

        private String getSimpleName(String name) {
            int lastDot = name.lastIndexOf('.');
            if (lastDot > 0) {
                return name.substring(lastDot + 1);
            } else {
                return name;
            }
        }

        @Override
        public String toString() {
            return "MethodMatcher{" +
                    "classSimpleName='" + classSimpleName + '\'' +
                    ", methodSimpleName='" + methodSimpleName + '\'' + '}';
        }
    }

    public List<String> getPackages() {
        return packages;
    }

    private boolean classMatches(
            List<MethodMatcher> methodToStartProfiling,
            TypeDescription.Generic clazzType,
            String methodName)
    {
        for (MethodMatcher exec : methodToStartProfiling) {
            if (exec.matchesExact(clazzType, methodName)) {
                return true;
            }
        }
        return false;
    }
}
