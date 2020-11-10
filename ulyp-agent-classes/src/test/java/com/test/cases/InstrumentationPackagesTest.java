package com.test.cases;

import com.test.cases.a.A;
import com.test.cases.a.c.C;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InstrumentationPackagesTest extends AbstractInstrumentationTest {

    @Test
    public void shouldInstrumentAndTraceAllClasses() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(A.class)
                        .setInstrumentedPackages("com.test.cases.a")
        );

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is(A.class.getName()));
        assertThat(root.getChildren(), Matchers.hasSize(2));
    }

    @Test
    public void shouldExcludeInstrumentationPackage() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(A.class)
                        .setInstrumentedPackages("com.test.cases.a")
                        .setExcludedFromInstrumentationPackages("com.test.cases.a.b")
        );

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is("com.test.cases.a.A"));
        assertThat(root.getChildren(), Matchers.hasSize(1));

        CallRecord callRecord = root.getChildren().get(0);

        assertThat(callRecord.getClassName(), is(C.class.getName()));
        assertThat(callRecord.getMethodName(), is("c"));
    }

    @Test
    public void shouldExcludeTwoPackages() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(A.class)
                        .setInstrumentedPackages("com.test.cases.a")
                        .setExcludedFromInstrumentationPackages("com.test.cases.a.b", "com.test.cases.a.c")
        );

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is("com.test.cases.a.A"));
        assertThat(root.getChildren(), Matchers.empty());
    }
}
