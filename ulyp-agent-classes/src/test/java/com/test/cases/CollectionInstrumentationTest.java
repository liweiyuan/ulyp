package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CollectionInstrumentationTest extends AbstractInstrumentationTest {

    public static class CollectionTestCases {

        public void acceptsListOfStringsWithSize0(List<String> list) {

        }

        public void acceptsListOfStringsWithSize1(List<String> list) {

        }

        public static void main(String[] args) {
            SafeCaller.call(() -> new CollectionTestCases().acceptsListOfStringsWithSize0(Collections.emptyList()));
            SafeCaller.call(() -> new CollectionTestCases().acceptsListOfStringsWithSize1(Arrays.asList("a")));
        }
    }

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
