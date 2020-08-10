package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

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
                        .setMethodToTrace("main")
        );
    }
}
