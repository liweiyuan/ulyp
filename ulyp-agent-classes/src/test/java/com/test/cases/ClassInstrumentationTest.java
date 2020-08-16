package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordTree;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClassInstrumentationTest extends AbstractInstrumentationTest {

    static class X {}

    static class PassClazz {

        public static void pass(Class<?> clazz) {

        }

        public static void main(String[] args) {
            pass(X.class);
        }
    }

    @Test
    public void testClassTypePassing() {

        CallRecordTree tree = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(PassClazz.class)
                        .setMethodToRecord("pass")
        );

        CallRecord root = tree.getRoot();

        assertThat(root.getArgTexts(), is(Arrays.asList("class com.test.cases.ClassInstrumentationTest$X")));
    }
}
