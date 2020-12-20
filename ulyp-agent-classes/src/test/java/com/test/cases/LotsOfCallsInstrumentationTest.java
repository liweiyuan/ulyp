package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class LotsOfCallsInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldMake1000Calls() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(LotsOfCallsTestCases.class)
                        .setMethodToRecord("make1000CallsSep")
        );

        assertThat(root.getChildren(), hasSize(1000));
    }

    @Test
    @Ignore
    public void shouldMakeLessCallsIfLimitedByMaxCallsProperty() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(LotsOfCallsTestCases.class)
                        .setMethodToRecord("make1000CallsLevel0")
                        .setMaxCallsPerMethod(7)
        );

        assertThat(root.getChildren(), hasSize(1));
        assertThat(root.getChildren().get(0).getChildren(), hasSize(7));
    }

    @Test
    @Ignore
    public void shouldMakeLessCallsIfLimitedByMaxCallsProperty2() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(LotsOfCallsTestCases.class)
                        .setMethodToRecord("level0")
                        .setMaxCallsPerMethod(5)
        );

        assertThat(root.getChildren(), hasSize(5));
        assertThat(root.getChildren().get(0).getChildren(), hasSize(5));
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren(), hasSize(5));
    }

    public static class LotsOfCallsTestCases {

        private static volatile int calls = 1000;
        private static volatile int value;

        public static void main(String[] args) {
            SafeCaller.call(() -> new LotsOfCallsTestCases().make1000CallsLevel0());
            SafeCaller.call(() -> new LotsOfCallsTestCases().level0());
            SafeCaller.call(() -> new LotsOfCallsTestCases().make1000CallsSep());
        }

        public void level0() {
            for (int i = 0; i < 10; i++) {
                level1();
            }
        }

        public void level1() {
            for (int i = 0; i < 10; i++) {
                level2();
            }
        }

        public void level2() {
            for (int i = 0; i < 10; i++) {
                make1000Calls();
            }
        }

        public void make1000CallsLevel0() {
            make1000Calls();
        }

        public void make1000Calls() {
            for (int i = 0; i < calls; i++) {
                value = subCall();
            }
        }

        private int subCall() {
            return 2;
        }

        public void make1000CallsSep() {
            for (int i = 0; i < calls; i++) {
                value = subCall();
            }
        }
    }
}
