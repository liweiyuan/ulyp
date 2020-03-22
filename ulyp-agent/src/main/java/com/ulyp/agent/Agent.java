package com.ulyp.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Starting ULYP agent");

        Settings settings = BbTransformer.settings;

        ElementMatcher.Junction<NamedElement> j = null;

        for (int i = 0; i < settings.getPackages().size(); i++) {
            if (j == null) {
                j = ElementMatchers.nameStartsWith(settings.getPackages().get(i));
            } else {
                j = j.or(ElementMatchers.nameStartsWith(settings.getPackages().get(i)));
            }
        }

        new AgentBuilder.Default()
                .type(j)
                .transform(new BbTransformer())
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);
    }
}
