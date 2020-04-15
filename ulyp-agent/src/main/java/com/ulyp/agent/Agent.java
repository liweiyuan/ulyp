package com.ulyp.agent;

import com.ulyp.agent.log.AgentLogManager;
import com.ulyp.agent.log.LoggingSettings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Starting ULYP agent, logging level " + LoggingSettings.LOG_LEVEL.name());

        Settings settings = BbTransformer.settings;

        ElementMatcher.Junction<TypeDescription> matcherUserDefinedPackages = null;

        for (int i = 0; i < settings.getPackages().size(); i++) {
            if (matcherUserDefinedPackages == null) {
                matcherUserDefinedPackages = ElementMatchers.nameStartsWith(settings.getPackages().get(i));
            } else {
                matcherUserDefinedPackages = matcherUserDefinedPackages.or(ElementMatchers.nameStartsWith(settings.getPackages().get(i)));
            }
        }

        ElementMatcher.Junction<TypeDescription> finalMatcher = ElementMatchers
                .not(ElementMatchers.nameStartsWith("com.ulyp"))
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("shadowed")));

        if (matcherUserDefinedPackages != null) {
            finalMatcher = finalMatcher.and(matcherUserDefinedPackages);
        }

        AgentLogManager.getLogger(Agent.class).trace("Matcher for scanning is {}", finalMatcher);

        new AgentBuilder.Default()
                .type(finalMatcher)
                .transform(new BbTransformer())
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);
    }
}
