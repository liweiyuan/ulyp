package com.ulyp.agent;

import com.ulyp.agent.log.LoggingSettings;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.RecordingStartMethodList;
import com.ulyp.agent.settings.UiSettings;
import com.ulyp.core.util.PackageList;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.Level;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void start(String args, Instrumentation instrumentation) {

        String logLevel = LoggingSettings.LOG_LEVEL.name();
        AgentContext instance = AgentContext.getInstance();
        UiSettings uiSettings = instance.getUiSettings();
        SystemPropertiesSettings systemPropertiesSettings = SystemPropertiesSettings.load();

        PackageList instrumentedPackages = uiSettings.getInstrumentedPackages().getValue();
        PackageList excludedPackages = uiSettings.getExcludeFromInstrumentationPackages().getValue();
        RecordingStartMethodList recordingStartMethodList = uiSettings.getRecordingStartMethod().getValue();

        // TODO show that connected to UI (if connected)
        System.out.println("Starting ULYP agent, logging level = " + logLevel +
                ", packages = " + uiSettings.getInstrumentedPackages() +
                ", tracing start methods = " + uiSettings.getRecordingStartMethod());

        ElementMatcher.Junction<TypeDescription> tracingMatcher = null;

        for (String packageToInstrument : instrumentedPackages) {
            if (tracingMatcher == null) {
                tracingMatcher = ElementMatchers.nameStartsWith(packageToInstrument);
            } else {
                tracingMatcher = tracingMatcher.or(ElementMatchers.nameStartsWith(packageToInstrument));
            }
        }

        excludedPackages.add("java");
        excludedPackages.add("javax");
        excludedPackages.add("jdk");

        for (String excludedPackage : excludedPackages) {
            if (tracingMatcher == null) {
                tracingMatcher = ElementMatchers.not(ElementMatchers.nameStartsWith(excludedPackage));
            } else {
                tracingMatcher = tracingMatcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(excludedPackage)));
            }
        }

        ElementMatcher.Junction<TypeDescription> finalMatcher = ElementMatchers
                .not(ElementMatchers.nameStartsWith("com.ulyp"))
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("shadowed")));

        if (tracingMatcher != null) {
            finalMatcher = finalMatcher.and(tracingMatcher);
        }

//        AgentLogManager.getLogger(Agent.class).trace("Matcher for scanning is {}", finalMatcher);

        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .type(finalMatcher)
                .transform(new BbTransformer(MethodAdvice.class, recordingStartMethodList))
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE);

        if (LoggingSettings.LOG_LEVEL == Level.TRACE) {
            agentBuilder = agentBuilder.with(AgentBuilder.Listener.StreamWriting.toSystemOut());
        }
        agentBuilder.installOn(instrumentation);
    }
}
