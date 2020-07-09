package com.ulyp.agent;

import com.test.cases.AtomicNumbersTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AtomicNumbersTest extends AbstractInstrumentationTest {

    @Test
    public void testAtomicIntegerSum() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(AtomicNumbersTestCases.class)
                        .setMethodToTrace("intSum")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }

    @Test
    public void testBoxedDoubleSum() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(AtomicNumbersTestCases.class)
                        .setMethodToTrace("longSum")
        );


        CallTrace root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }
}
