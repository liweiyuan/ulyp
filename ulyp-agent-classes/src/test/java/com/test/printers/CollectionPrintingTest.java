package com.test.printers;

import com.test.cases.AbstractInstrumentationTest;
import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.printers.CollectionRepresentation;
import com.ulyp.core.printers.IdentityObjectRepresentation;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.core.printers.StringObjectRepresentation;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CollectionPrintingTest extends AbstractInstrumentationTest {

    @Test
    public void shouldRecordSimpleList() {

        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(TestCase.class)
                        .setMethodToRecord("returnArrayListOfString")
                        .recordCollections()
        );

        CollectionRepresentation collection = (CollectionRepresentation) root.getReturnValue();

        List<ObjectRepresentation> items = collection.getRecordedItems();

        StringObjectRepresentation firstItemRepr = (StringObjectRepresentation) items.get(0);
        Assert.assertEquals("a", firstItemRepr.print());

        StringObjectRepresentation secondItemRepr = (StringObjectRepresentation) items.get(1);
        Assert.assertEquals("b", secondItemRepr.print());
    }

    @Test
    public void shouldFallbackToIdentityIfRecordingFailed() {

        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(TestCase.class)
                        .setMethodToRecord("returnThrowingOnIteratorList")
                        .recordCollections()
        );

        Assert.assertThat(root.getReturnValue(), Matchers.instanceOf(IdentityObjectRepresentation.class));
    }

    static class TestCase {

        public static List<String> returnArrayListOfString() {
            return Arrays.asList("a", "b");
        }

        public static List<String> returnThrowingOnIteratorList() {
            return new ArrayList<String>() {
                {
                    add("a");
                    add("b");
                }

                @NotNull
                @Override
                public Iterator<String> iterator() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public static void main(String[] args) {
            System.out.println(returnArrayListOfString());
            List<String> strings = returnThrowingOnIteratorList();
            System.out.println(System.identityHashCode(strings));
        }
    }
}
