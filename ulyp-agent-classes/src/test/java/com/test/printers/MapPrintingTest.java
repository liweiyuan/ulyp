package com.test.printers;

import com.test.cases.AbstractInstrumentationTest;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.*;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class MapPrintingTest extends AbstractInstrumentationTest {

    @Test
    public void shouldRecordSimpleMap() {

        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(TestCase.class)
                        .setMethodToRecord("returnHashMap")
                        .recordCollections()
        );

        MapRepresentation collection = (MapRepresentation) root.getReturnValue();

        Assert.assertEquals(2, collection.getSize());

        List<MapEntryRepresentation> entries = collection.getEntries();
        MapEntryRepresentation firstEntry = entries.get(0);
        StringObjectRepresentation key = (StringObjectRepresentation) firstEntry.getKey();
        Assert.assertEquals("a", key.getValue());
        StringObjectRepresentation value = (StringObjectRepresentation) firstEntry.getValue();
        Assert.assertEquals("b", value.getValue());
    }

    @Test
    public void shouldFallbackToIdentityIfRecordingFailed() {

        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(TestCase.class)
                        .setMethodToRecord("returnMapThrowingOnIteration")
                        .recordCollections()
        );

        Assert.assertThat(root.getReturnValue(), Matchers.instanceOf(IdentityObjectRepresentation.class));
    }

    static class TestCase {

        public static Map<String, String> returnHashMap() {
            return new LinkedHashMap<String, String>() {
                {
                    put("a", "b");
                    put("c", "d");
                }
            };
        }

        public static Map<String, String> returnMapThrowingOnIteration() {
            return new HashMap<String, String>() {
                {
                    put("a", "b");
                    put("c", "d");
                }

                @Override
                public Set<Entry<String, String>> entrySet() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public static void main(String[] args) {
            System.out.println(returnHashMap());
            Map<String, String> stringStringMap = returnMapThrowingOnIteration();
            System.out.println(System.identityHashCode(stringStringMap));
        }
    }
}
