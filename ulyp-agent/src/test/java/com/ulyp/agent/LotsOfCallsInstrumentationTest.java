package com.ulyp.agent;

import com.test.cases.LotsOfCallsTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class LotsOfCallsInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldMake1000Calls() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(LotsOfCallsTestCases.class)
                        .setMethodToTrace("make1000CallsSep")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), hasSize(1000));
    }

    @Test
    public void shouldMakeLessCallsIfLimitedByMaxCallsProperty() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(LotsOfCallsTestCases.class)
                        .setMethodToTrace("make1000CallsLevel0")
                        .setMaxCallsPerMethod(7)
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), hasSize(1));
        assertThat(root.getChildren().get(0).getChildren(), hasSize(7));
    }

    @Test
    public void shouldMakeLessCallsIfLimitedByMaxCallsProperty2() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(LotsOfCallsTestCases.class)
                        .setMethodToTrace("level0")
                        .setMaxCallsPerMethod(5)
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getChildren(), hasSize(5));
        assertThat(root.getChildren().get(0).getChildren(), hasSize(5));
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren(), hasSize(5));
    }
}
