package com.ulyp.agent;

import com.ulyp.agent.settings.RecordingStartMethodList;
import com.ulyp.agent.settings.UiSettings;
import com.ulyp.core.log.LogLevel;
import com.ulyp.core.log.LoggingSettings;
import com.ulyp.core.process.ProcessInfo;
import com.ulyp.core.util.ClassUtils;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class Agent {

    private static final String ULYP_LOGO =
                    "   __  __    __ __  __    ____ \n" +
                    "  / / / /   / / \\ \\/ /   / __ \\\n" +
                    " / / / /   / /   \\  /   / /_/ /\n" +
                    "/ /_/ /   / /___ / /   / ____/ \n" +
                    "\\____/   /_____//_/   /_/      \n" +
                    "                               ";

    public static void start(String args, Instrumentation instrumentation) {

        if (AgentContext.isLoaded()) {
            return;
        }
        AgentContext.load();

        String logLevel = LoggingSettings.LOG_LEVEL.name();
        AgentContext instance = AgentContext.getInstance();
        UiSettings uiSettings = instance.getUiSettings();

        PackageList instrumentedPackages = uiSettings.getInstrumentedPackages().getValue();
        PackageList excludedPackages = uiSettings.getExcludeFromInstrumentationPackages().getValue();
        RecordingStartMethodList recordingStartMethodList = uiSettings.getRecordingStartMethod().getValue();

        if (recordingStartMethodList == null || recordingStartMethodList.isEmpty()) {
            // if not specified, then record main(String[] args) method as it's the only entry point to the program we have
            ProcessInfo processInfo = instance.getProcessInfo();
            recordingStartMethodList = new RecordingStartMethodList(
                    new MethodMatcher(ClassUtils.getSimpleNameFromName(processInfo.getMainClassName()), "main")
            );
        }

        System.out.println(ULYP_LOGO);
        System.out.println("Successfully connected to UI, logging level = " + logLevel +
                ", instrumentation packages = " + uiSettings.getInstrumentedPackages() +
                ", recording will start at " + uiSettings.getRecordingStartMethod());

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

        MethodDescriptionFactory methodDescriptionFactory = new MethodDescriptionFactory(recordingStartMethodList);

        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .type(finalMatcher)
                .transform((builder, typeDescription, classLoader, module) -> builder.visit(
                        Advice.withCustomMapping()
                                .bind(methodDescriptionFactory)
                                .to(MethodCallRecordingAdvice.class)
                                .on(ElementMatchers
                                        .isMethod()
                                        .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                                        .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                                )
                ))
                /*.transform((builder, typeDescription, classLoader, module) -> builder.visit(
                        Advice.withCustomMapping()
                                .bind(methodDescriptionFactory)
                                .to(ConstructorCallRecordingAdvice.class)
                                .on(ElementMatchers.isConstructor())
                ))*/
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE);
                // .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED);

        if (LoggingSettings.LOG_LEVEL == LogLevel.TRACE) {
            agentBuilder = agentBuilder.with(AgentBuilder.Listener.StreamWriting.toSystemOut());
        }
        agentBuilder.installOn(instrumentation);
    }
}
