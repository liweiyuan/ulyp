package com.ulyp.agent;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UploadingTransport;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Settings {

    private static final Settings instance = loadFromSystemProperties();

    public static Settings getInstance() {
        return instance;
    }

    private static Settings loadFromSystemProperties() {
        String packagesToInstrument = System.getProperty(PACKAGES_PROPERTY);
        List<String> packages;
        if (packagesToInstrument == null) {
            packages = Collections.emptyList();
        } else {
            packages = new ArrayList<>(Arrays.asList(packagesToInstrument.split(",")));
        }

        String tracedMethods = System.getProperty(START_METHOD_PROPERTY);
        List<MethodMatcher> tracingStartMethods;

        if (tracedMethods != null) {
            tracingStartMethods = Arrays.stream(tracedMethods.split(","))
                    .map(str -> new MethodMatcher(
                            str.substring(0, str.indexOf('.')),
                            str.substring(str.indexOf('.') + 1))
                    ).collect(Collectors.toList());
        } else {
            tracingStartMethods = Collections.emptyList();
        }

        String uiHost = System.getProperty(UI_HOST_PROPERTY, UploadingTransport.DEFAULT_ADDRESS.hostName);
        int uiPort = Integer.parseInt(System.getProperty(UI_PORT_PROPERTY, String.valueOf(UploadingTransport.DEFAULT_ADDRESS.port)));

        int maxTreeDepth = Integer.parseInt(System.getProperty(MAX_DEPTH_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
        int minTraceCount = Integer.parseInt(System.getProperty(MIN_TRACE_COUNT, String.valueOf(1)));
        return new Settings(new UiAddress(uiHost, uiPort), packages, tracingStartMethods, maxTreeDepth, minTraceCount);
    }

    public static final String PACKAGES_PROPERTY = "ulyp.packages";
    public static final String START_METHOD_PROPERTY = "ulyp.start-method";
    public static final String UI_HOST_PROPERTY = "ulyp.ui-host";
    public static final String UI_PORT_PROPERTY = "ulyp.ui-port";
    public static final String MAX_DEPTH_PROPERTY = "ulyp.max-depth";
    public static final String MIN_TRACE_COUNT = "ulyp.min-trace-count";

    private UiAddress uiAddress;
    private final List<String> packages;
    private final List<MethodMatcher> startTracingMethods;
    private final int maxTreeDepth;
    private final int minTraceCount;

    private Settings(
            UiAddress uiAddress,
            List<String> packages,
            List<MethodMatcher> startTracingMethods,
            int maxTreeDepth,
            int minTraceCount)
    {
        this.uiAddress = uiAddress;
        this.packages = packages;
        this.startTracingMethods = startTracingMethods;
        this.maxTreeDepth = maxTreeDepth;
        this.minTraceCount = minTraceCount;
    }

    public UiAddress getUiAddress() {
        return uiAddress;
    }

    public boolean shouldStartTracing(MethodDescription description) {
        return startTracingMethods.isEmpty() || methodMatches(startTracingMethods, description);
    }

    public boolean methodMatches(List<MethodMatcher> methodsToMatch, MethodDescription description) {
        try {
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

    public int getMinTraceCount() {
        return minTraceCount;
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
