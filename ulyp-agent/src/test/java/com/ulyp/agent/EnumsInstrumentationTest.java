package com.ulyp.agent;

import com.test.cases.EnumTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnumsInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintEnumNames() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(EnumTestCases.class)
                        .setMethodToTrace("consumesMapAndEnums")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(3));
        assertThat(root.getArgTexts().get(1), is("T1"));
        assertThat(root.getArgTexts().get(2), is("T2"));
    }
}
