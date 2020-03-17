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

    public static final String PACKAGES_PROPERTY = "ulyp.packages";
    public static final String START_METHOD_PROPERTY = "ulyp.start-method";
    public static final String LOG_PROPERTY = "ulyp.log";
    public static final String UI_HOST_PROPERTY = "ulyp.ui-host";
    public static final String UI_PORT_PROPERTY = "ulyp.ui-port";
    public static final String MAX_DEPTH_PROPERTY = "ulyp.max-depth";

    private UiAddress uiAddress;
    private final List<String> packages;
    private final List<MethodMatcher> methodToStartProfiling;
    private final boolean loggingTurnedOn;
    private final int maxTreeDepth;

    public Settings(
            UiAddress uiAddress,
            List<String> packages,
            List<MethodMatcher> methodToStartProfiling,
            boolean loggingTurnedOn,
            int maxTreeDepth)
    {
        this.uiAddress = uiAddress;
        this.packages = packages;
        this.methodToStartProfiling = methodToStartProfiling;
        this.loggingTurnedOn = loggingTurnedOn;
        this.maxTreeDepth = maxTreeDepth;
    }

    public UiAddress getUiAddress() {
        return uiAddress;
    }

    public boolean loggingTurnedOn() {
        return loggingTurnedOn;
    }

    public static Settings fromJavaProperties() {
        String packagesToInstrument = System.getProperty(PACKAGES_PROPERTY);
        if (packagesToInstrument == null) {
            throw new RuntimeException("Please specify property " + PACKAGES_PROPERTY + " in the following format -D" + PACKAGES_PROPERTY + "=com.example,org.hibernate");
        }
        List<String> packages = new ArrayList<>(Arrays.asList(packagesToInstrument.split(",")));
        System.out.println("Packages: " + packages);

        String tracedMethods = System.getProperty(START_METHOD_PROPERTY);
        if (tracedMethods == null) {
            throw new RuntimeException("Please specify property " + START_METHOD_PROPERTY);
        }

        // TODO validate
        List<MethodMatcher> matchers = Arrays.stream(tracedMethods.split(","))
                .map(str -> new MethodMatcher(
                        str.substring(0, str.indexOf('.')),
                        str.substring(str.indexOf('.') + 1))
                ).collect(Collectors.toList());

        boolean loggingTurnedOn = System.getProperty(LOG_PROPERTY) != null;
        String uiHost = System.getProperty(UI_HOST_PROPERTY, UploadingTransport.DEFAULT_ADDRESS.hostName);
        int uiPort = Integer.parseInt(System.getProperty(UI_PORT_PROPERTY, String.valueOf(UploadingTransport.DEFAULT_ADDRESS.port)));

        int maxTreeDepth = Integer.parseInt(System.getProperty("ulyp.max-depth", String.valueOf(Integer.MAX_VALUE)));
        return new Settings(new UiAddress(uiHost, uiPort), packages, matchers, loggingTurnedOn, maxTreeDepth);
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
            return "MethodMatcher{classSimpleName='" + classSimpleName + '\'' + ", methodSimpleName='" + methodSimpleName + '\'' + '}';
        }
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
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
