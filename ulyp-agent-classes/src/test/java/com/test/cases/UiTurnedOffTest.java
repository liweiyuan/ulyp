package com.test.cases;

import org.junit.Test;

public class UiTurnedOffTest extends AbstractInstrumentationTest {

    @Test
    public void shouldNotConnectToUiIfExplicitlyUiTurnedOff() {

        // runSubprocessAndExpectNotConnected(new TestSettingsBuilder().setMainClassName(X.class));
    }

    static class X {

        public static void main(String[] args) {

        }
    }
}
