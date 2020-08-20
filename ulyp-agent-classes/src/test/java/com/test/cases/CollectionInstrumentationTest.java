package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CollectionInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(CollectionTestCases.class)
                        .setMethodToRecord("acceptsListOfStringsWithSize0")
        );

        assertThat(root.getArgTexts(), is(Arrays.asList("EmptyList{}")));
    }

    public static class CollectionTestCases {

        public static void main(String[] args) {
            SafeCaller.call(() -> new CollectionTestCases().acceptsListOfStringsWithSize0(Collections.emptyList()));
            SafeCaller.call(() -> new CollectionTestCases().acceptsListOfStringsWithSize1(Arrays.asList("a")));
        }

        public void acceptsListOfStringsWithSize0(List<String> list) {

        }

        public void acceptsListOfStringsWithSize1(List<String> list) {

        }
    }
}
