package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallTrace;
import com.ulyp.core.CallTraceTree;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

public class UserDefinedClassInstrumentationTest extends AbstractInstrumentationTest {

    public static class UserDefinedClassTestCases {

        public class TestClass {

        }

        public static class ToStringCallsSelf {

            private final String name;
            private final String secondName;

            public ToStringCallsSelf(String name, String secondName) {
                this.name = name;
                this.secondName = secondName;
            }

            public String getName() {
                return name;
            }

            public String getSecondName() {
                return secondName;
            }

            @Override
            public String toString() {
                return "ToStringCallsSelf{" +
                        "name='" + getName() + '\'' +
                        ", secondName='" + getSecondName() + '\'' +
                        '}';
            }
        }

        UserDefinedClassTestCases.TestClass returnInnerClass() {
            return new UserDefinedClassTestCases.TestClass();
        }

        UserDefinedClassTestCases.ToStringCallsSelf returnClassThatCallsSelfInToString(UserDefinedClassTestCases.ToStringCallsSelf v1, UserDefinedClassTestCases.ToStringCallsSelf v2) {
            return new UserDefinedClassTestCases.ToStringCallsSelf(v1.toString() + v2.toString(), v1.toString() + "/" + v2.toString());
        }

        public static void main(String[] args) {
            SafeCaller.call(() -> new UserDefinedClassTestCases().returnInnerClass());
            SafeCaller.call(() -> new UserDefinedClassTestCases().returnClassThatCallsSelfInToString(
                    new UserDefinedClassTestCases.ToStringCallsSelf("n1", "s1"),
                    new UserDefinedClassTestCases.ToStringCallsSelf("n1", "s1")
            ));
        }
    }

    @Test
    public void shouldPrintEnumNames() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(UserDefinedClassTestCases.class)
                        .setMethodToTrace("returnInnerClass")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getResult(), matchesPattern("TestClass@\\d+"));
    }

    @Test
    public void shouldNotFailIfToStringCallsTracedMethod() {
        CallTraceTree tree = executeClass(
                new TestSettingsBuilder()
                        .setMainClassName(UserDefinedClassTestCases.class)
                        .setMethodToTrace("returnClassThatCallsSelfInToString")
        );

        CallTrace root = tree.getRoot();

        assertThat(root.getResult(), is("ToStringCallsSelf{name='ToStringCallsSelf{name='n1', secondName='s1'}ToStringCallsSelf{name='n1', secondName='s1'}', secondName='ToStringCallsSelf{name='n1', secondName='s1'}/ToStringCallsSelf{name='n1', secondName='s1'}'}"));
    }
}
