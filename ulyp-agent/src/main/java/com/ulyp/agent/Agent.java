package com.ulyp.agent;

import com.ulyp.agent.log.AgentLogManager;
import com.ulyp.agent.log.LoggingSettings;
import com.ulyp.agent.settings.AgentSettings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {

        String logLevel = LoggingSettings.LOG_LEVEL.name();
        AgentSettings settings = BbTransformer.settings;

        System.out.println("Starting ULYP agent, logging level = " + logLevel + ", settings = " + settings);

        ElementMatcher.Junction<TypeDescription> packageMatcher = null;

        for (int i = 0; i < settings.getPackages().size(); i++) {
            if (packageMatcher == null) {
                packageMatcher = ElementMatchers.nameStartsWith(settings.getPackages().get(i));
            } else {
                packageMatcher = packageMatcher.or(ElementMatchers.nameStartsWith(settings.getPackages().get(i)));
            }
        }
        for (int i = 0; i < settings.getExcludePackages().size(); i++) {
            if (packageMatcher == null) {
                packageMatcher = ElementMatchers.not(ElementMatchers.nameStartsWith(settings.getPackages().get(i)));
            } else {
                packageMatcher = packageMatcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(settings.getPackages().get(i))));
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
