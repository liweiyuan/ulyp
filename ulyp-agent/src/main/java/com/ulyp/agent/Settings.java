package com.ulyp.agent;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UploadingTransport;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.apache.commons.lang3.StringUtils;

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
        String excludedPackagesStr = System.getProperty(EXCLUDE_PACKAGES_PROPERTY);
        List<String> excludedPackages;
        if (excludedPackagesStr != null) {
            excludedPackages = new ArrayList<>(Arrays.asList(excludedPackagesStr.split(",")));
        } else {
            excludedPackages = Collections.emptyList();
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
        int maxCallPerMethod = Integer.parseInt(System.getProperty(MAX_CALL_PER_METHOD, String.valueOf(Integer.MAX_VALUE / 2)));
        int minTraceCount = Integer.parseInt(System.getProperty(MIN_TRACE_COUNT, String.valueOf(1)));
        return new Settings(new UiAddress(uiHost, uiPort), packages, excludedPackages, tracingStartMethods, maxTreeDepth, maxCallPerMethod, minTraceCount);
    }

    public static final String PACKAGES_PROPERTY = "ulyp.packages";
    public static final String EXCLUDE_PACKAGES_PROPERTY = "ulyp.exclude-packages";
    public static final String START_METHOD_PROPERTY = "ulyp.start-method";
    public static final String UI_HOST_PROPERTY = "ulyp.ui-host";
    public static final String UI_PORT_PROPERTY = "ulyp.ui-port";
    public static final String MAX_DEPTH_PROPERTY = "ulyp.max-depth";
    public static final String MAX_CALL_PER_METHOD = "ulyp.max-calls-per-method";
    public static final String MIN_TRACE_COUNT = "ulyp.min-trace-count";

    private UiAddress uiAddress;
    private final List<String> packages;
    private final List<String> excludePackages;
    private final List<MethodMatcher> startTracingMethods;
    private final int maxTreeDepth;
    private final int maxCallsPerMethod;
    private final int minTraceCount;

    public Settings(
            UiAddress uiAddress,
            List<String> packages,
            List<String> excludePackages,
            List<MethodMatcher> startTracingMethods,
            int maxTreeDepth,
            int maxCallsPerMethod,
            int minTraceCount)
    {
        this.uiAddress = uiAddress;
        this.packages = packages;
        this.excludePackages = excludePackages;
        this.startTracingMethods = startTracingMethods;
        this.maxTreeDepth = maxTreeDepth;
        this.maxCallsPerMethod = maxCallsPerMethod;
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

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public int getMinTraceCount() {
        return minTraceCount;
    }

    public int getMaxCallsPerMethod() {
        return maxCallsPerMethod;
    }

    public List<String> getPackages() {
        return packages;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
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

    public List<String> toCmdJavaProps() {
        List<String> params = new ArrayList<>();

        params.add("-D" + Settings.PACKAGES_PROPERTY + "=" + String.join(",", packages));
        if (excludePackages.isEmpty()) {
            params.add("-D" + Settings.EXCLUDE_PACKAGES_PROPERTY + "=" + String.join(",", excludePackages));
        }

        params.add("-D" + Settings.START_METHOD_PROPERTY + "=" + startTracingMethods.stream().map(MethodMatcher::toString).collect(Collectors.joining()));
        params.add("-D" + Settings.UI_PORT_PROPERTY + "=" + uiAddress.port);
        params.add("-D" + Settings.MAX_DEPTH_PROPERTY + "=" + maxTreeDepth);
        params.add("-D" + Settings.MIN_TRACE_COUNT + "=" + minTraceCount);
        params.add("-D" + Settings.MAX_CALL_PER_METHOD + "=" + maxCallsPerMethod);

        return params;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "uiAddress=" + uiAddress +
                ", packages=" + packages +
                ", excludePackages=" + excludePackages +
                ", startTracingMethods=" + startTracingMethods +
                ", maxTreeDepth=" + maxTreeDepth +
                ", maxCallsPerMethod=" + maxCallsPerMethod +
                ", minTraceCount=" + minTraceCount +
                '}';
    }
}
