package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UiTurnedOffTest extends AbstractInstrumentationTest {

    static class X {

        public static void main(String[] args) {

        }
    }

    @Test
    public void shouldNotConnectToUiIfExplicitlyUiTurnedOff() {

        runSubprocessAndExpectNotConnected(
                new TestSettingsBuilder()
                        .setMainClassName(X.class)
                        .setMethodToRecord("main")
        );
    }
}
