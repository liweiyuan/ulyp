package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import org.junit.Test;

public class UiTurnedOffTest extends AbstractInstrumentationTest {

    @Test
    public void shouldNotConnectToUiIfExplicitlyUiTurnedOff() {

        runSubprocessAndExpectNotConnected(
                new TestSettingsBuilder()
                        .setMainClassName(X.class)
                        .setMethodToRecord("main")
        );
    }

    static class X {

        public static void main(String[] args) {

        }
    }
}
