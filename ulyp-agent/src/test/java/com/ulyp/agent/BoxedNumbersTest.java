package com.ulyp.agent;

import com.test.cases.BoxedNumbersTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BoxedNumbersTest extends AbstractInstrumentationTest {

    @Test
    public void testPrimitiveIntSum() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                .setMethodToTrace("primitiveIntSum")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }

    @Test
    public void testBoxedIntSum() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToTrace("boxedIntSum")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }

    @Test
    public void testPrimitiveDoubleSum() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToTrace("primitiveDoubleSum")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue().getPrintedText(), is("-5112.9847"));
    }

    @Test
    public void testBoxedDoubleSum() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToTrace("boxedDoubleSum")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue().getPrintedText(), is("-5112.9847"));
    }
}
