package com.ulyp.agent;

import com.test.cases.CollectionTestCases;
import com.ulyp.agent.util.MethodTraceTree;
import com.ulyp.agent.util.MethodTraceTreeNode;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CollectionInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldProvideArgumentTypes() {
        MethodTraceTree tree = executeClass(
                CollectionTestCases.class,
                "com.test.cases",
                "CollectionTestCases.acceptsListOfStringsWithSize0"
        );

        MethodTraceTreeNode root = tree.getRoot();

        assertThat(root.getArgs(), is(Arrays.asList("EmptyList{}")));
    }
}
