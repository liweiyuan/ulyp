package com.ulyp.agent;

import com.test.cases.ObjectArrayTestCases;
import com.ulyp.agent.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ObjectArrayInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptEmptyObjectArray")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("Object[]"));
    }

    @Test
    public void testUserDefinedEmptyArray() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptEmptyUserDefinedClassArray")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("X[]"));
    }

    @Test
    public void testUserDefinedClassArrayWith3Elements() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptUserDefinedClassArrayWith3Elements")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("X[][3 items ]"));
    }

    @Test
    public void testUserDefinedClassArrayWith3ElementsWithArrayTrace() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setTraceCollections(true)
                        .setMainClassName(ObjectArrayTestCases.class)
                        .setMethodToTrace("acceptUserDefinedClassArrayWith3Elements")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getArgs().get(0).getPrintedText(), is("[X{text='a'}, null, X{text='b'}]"));
    }
}
