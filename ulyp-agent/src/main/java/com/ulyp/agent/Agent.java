package com.ulyp.agent;

import com.ulyp.agent.log.LoggingSettings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Starting ULYP agent, logging level " + LoggingSettings.LOG_LEVEL.name());

        Settings settings = BbTransformer.settings;

        ElementMatcher.Junction<NamedElement> matcher = null;

        for (int i = 0; i < settings.getPackages().size(); i++) {
            if (matcher == null) {
                matcher = ElementMatchers.nameStartsWith(settings.getPackages().get(i));
            } else {
                matcher = matcher.or(ElementMatchers.nameStartsWith(settings.getPackages().get(i)));
            }
        }

        new AgentBuilder.Default()
                .type(matcher)
                .transform(new BbTransformer())
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);
    }
}
