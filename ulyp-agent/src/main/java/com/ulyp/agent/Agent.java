package com.ulyp.agent;

import com.ulyp.agent.log.AgentLogManager;
import com.ulyp.agent.log.LoggingSettings;
import com.ulyp.agent.settings.AgentSettings;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.UiSettings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.List;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {

        String logLevel = LoggingSettings.LOG_LEVEL.name();
        AgentContext instance = AgentContext.getInstance();
        SystemPropertiesSettings systemPropSettings = SystemPropertiesSettings.loadFromSystemProperties();
        UiSettings uiSettings = instance.getUiSettings();
        List<String> tracePackages = uiSettings.getTracePackages().getValue();

        // TODO show that connected to UI (if connected)
        System.out.println("Starting ULYP agent, logging level = " + logLevel +
                ", packages = " + uiSettings.getTracePackages() +
                ", tracing start methods = " + uiSettings.getTracingStartMethod());

        ElementMatcher.Junction<TypeDescription> packageMatcher = null;

        for (String tracePackage : tracePackages) {
            if (packageMatcher == null) {
                packageMatcher = ElementMatchers.nameStartsWith(tracePackage);
            } else {
                packageMatcher = packageMatcher.or(ElementMatchers.nameStartsWith(tracePackage));
            }
        }
        for (int i = 0; i < systemPropSettings.getExcludePackages().size(); i++) {
            if (packageMatcher == null) {
                packageMatcher = ElementMatchers.not(ElementMatchers.nameStartsWith(systemPropSettings.getPackages().get(i)));
            } else {
                packageMatcher = packageMatcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(systemPropSettings.getPackages().get(i))));
            }
        }

        ElementMatcher.Junction<TypeDescription> finalMatcher = ElementMatchers
                .not(ElementMatchers.nameStartsWith("com.ulyp"))
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("shadowed")));

        if (packageMatcher != null) {
            finalMatcher = finalMatcher.and(packageMatcher);
        }

        AgentLogManager.getLogger(Agent.class).trace("Matcher for scanning is {}", finalMatcher);

        new AgentBuilder.Default()
                .type(finalMatcher)
                .transform(new BbTransformer())
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);
    }
}
