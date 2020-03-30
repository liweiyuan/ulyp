package com.ulyp.agent;

import com.test.cases.ObjectTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

public class ObjectInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintObjects() {
        MethodTraceTree tree = executeClass(
                ObjectTestCases.class,
                "com.test.cases",
                "ObjectTestCases.acceptsTwoObjects"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0), matchesPattern("Object\\{ java\\.lang\\.Object@.+\\}"));
        assertThat(root.getArgs().get(1), matchesPattern("Object\\{ java\\.lang\\.Object@.+\\}"));
    }

    @Test
    public void shouldChooseValidPrinterForJavaLangObjectAtRuntime() {
        MethodTraceTree tree = executeClass(
                ObjectTestCases.class,
                "com.test.cases",
                "ObjectTestCases.acceptsTwoObjects2"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0), is("asdasd"));
        assertThat(root.getArgs().get(1), is("34"));
    }

    @Test
    public void shouldPrintNullArguments() {
        MethodTraceTree tree = executeClass(
                ObjectTestCases.class,
                "com.test.cases",
                "ObjectTestCases.acceptsTwoNulls"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), Matchers.hasSize(2));
        assertThat(root.getArgs().get(0), is("null"));
        assertThat(root.getArgs().get(1), is("null"));
    }
}
