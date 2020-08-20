package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BoxedNumbersTest extends AbstractInstrumentationTest {

    public static class BoxedNumbersTestCases {

        public static int primitiveIntSum(int v1, int v2) {
            return v1 + v2;
        }

        public static double primitiveDoubleSum(double v1, double v2) {
            return v1 + v2;
        }

        public static Integer boxedIntSum(Integer v1, Integer v2) {
            return v1 + v2;
        }

        public static Double boxedDoubleSum(Double v1, Double v2) {
            return v1 + v2;
        }

        public static void te(Long v1) {

        }

        public static void main(String[] args) {
            SafeCaller.call(() -> BoxedNumbersTestCases.boxedIntSum(Integer.valueOf(-234), Integer.valueOf(23)));
            SafeCaller.call(() -> BoxedNumbersTestCases.boxedDoubleSum(Double.valueOf(-5434.23), Double.valueOf(321.2453)));
            SafeCaller.call(() -> BoxedNumbersTestCases.primitiveDoubleSum(Double.valueOf(-5434.23), Double.valueOf(321.2453)));
            SafeCaller.call(() -> BoxedNumbersTestCases.primitiveIntSum(-234, 23));
            SafeCaller.call(() -> BoxedNumbersTestCases.te(1L));
        }
    }

    @Test
    public void test() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToRecord("te")
        );

        assertThat(root.getArgTexts(), is(Arrays.asList("1")));
    }

    @Test
    public void testPrimitiveIntSum() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToRecord("primitiveIntSum")
        );

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }

    @Test
    public void testBoxedIntSum() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToRecord("boxedIntSum")
        );

        assertThat(root.getArgTexts(), is(Arrays.asList("-234", "23")));
        assertThat(root.getReturnValue().getPrintedText(), is("-211"));
    }

    @Test
    public void testPrimitiveDoubleSum() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToRecord("primitiveDoubleSum")
        );

        assertThat(root.getArgTexts(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue().getPrintedText(), is("-5112.9847"));
    }

    @Test
    public void testBoxedDoubleSum() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder().setMainClassName(BoxedNumbersTestCases.class)
                        .setMethodToRecord("boxedDoubleSum")
        );

        assertThat(root.getArgTexts(), is(Arrays.asList("-5434.23", "321.2453")));
        assertThat(root.getReturnValue().getPrintedText(), is("-5112.9847"));
    }
}
