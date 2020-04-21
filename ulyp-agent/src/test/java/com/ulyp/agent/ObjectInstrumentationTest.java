package com.ulyp.agent;

import com.test.cases.ObjectTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

public class ObjectInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintObjects() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToTrace("acceptsTwoObjects")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0).getPrintedText(), matchesPattern("Object@.+"));
    }

    @Test
    public void shouldChooseValidPrinterForJavaLangObjectAtRuntime() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToTrace("acceptsTwoObjects2")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0).getPrintedText(), is("asdasd"));
        assertThat(root.getArgs().get(1).getPrintedText(), is("34"));
    }

    @Test
    public void shouldCallToStringIfPossible() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToTrace("acceptsTwoObjects3")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0).getPrintedText(), matchesPattern("X@.+"));
        assertThat(root.getArgs().get(1).getPrintedText(), is("Y{}"));
    }

    @Test
    public void shouldPrintNullArguments() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectTestCases.class)
                        .setMethodToTrace("acceptsTwoNulls")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgTexts().get(0), is("null"));
        assertThat(root.getArgTexts().get(1), is("null"));
    }
}
