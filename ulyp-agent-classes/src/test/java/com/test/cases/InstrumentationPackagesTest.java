package com.test.cases;

import com.test.cases.a.A;
import com.test.cases.a.c.C;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InstrumentationPackagesTest extends AbstractInstrumentationTest {

    @Test
    public void shouldInstrumentAndTraceAllClasses() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(A.class)
                        .setInstrumentedPackages(Collections.singletonList("com.test.cases.a"))
                        .setMethodToTrace("main")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is(A.class.getName()));
        assertThat(root.getChildren(), Matchers.hasSize(2));
    }

    @Test
    public void shouldExcludeInstrumentationPackage() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(A.class)
                        .setInstrumentedPackages(Collections.singletonList("com.test.cases.a"))
                        .setExcludedFromInstrumentationPackages(Collections.singletonList("com.test.cases.a.b"))
                        .setMethodToTrace("main")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is("com.test.cases.a.A"));
        assertThat(root.getChildren(), Matchers.hasSize(1));

        CallTrace callTrace = root.getChildren().get(0);

        assertThat(callTrace.getClassName(), is(C.class.getName()));
        assertThat(callTrace.getMethodName(), is("c"));
    }

    @Test
    public void shouldExcludeTwoPackages() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(A.class)
                        .setInstrumentedPackages(Collections.singletonList("com.test.cases.a"))
                        .setExcludedFromInstrumentationPackages(Arrays.asList("com.test.cases.a.b", "com.test.cases.a.c"))
                        .setMethodToTrace("main")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getMethodName(), is("main"));
        assertThat(root.getClassName(), is("com.test.cases.a.A"));
        assertThat(root.getChildren(), Matchers.empty());
    }
}
