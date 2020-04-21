package com.ulyp.agent;

import com.test.cases.CollectionTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CollectionInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(CollectionTestCases.class)
                        .setMethodToTrace("acceptsListOfStringsWithSize0")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("EmptyList{}")));
    }
}
