package com.ulyp.agent;

import com.test.cases.AtomicNumbersTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClassDescriptionTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder().setMainClassName(AtomicNumbersTestCases.class)
                        .setMethodToTrace("intSum")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("-234"));
        assertThat(root.getArgs().get(0).getClassDescription().getSimpleName(), is("AtomicInteger"));
        assertThat(root.getArgs().get(0).getClassDescription().getName(), is("java.util.concurrent.atomic.AtomicInteger"));

        assertThat(root.getArgs().get(1).getPrintedText(), is("23"));
        assertThat(root.getArgs().get(1).getClassDescription().getSimpleName(), is("AtomicInteger"));
        assertThat(root.getArgs().get(1).getClassDescription().getName(), is("java.util.concurrent.atomic.AtomicInteger"));
    }
}
