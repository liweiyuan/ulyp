package com.test.cases;

import com.test.cases.util.TestSettingsBuilder;
import com.ulyp.core.CallRecord;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

public class UserDefinedClassInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldPrintEnumNames() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(UserDefinedClassTestCases.class)
                        .setMethodToRecord("returnInnerClass")
        );

// TODO use enum repr
        assertThat(root.getReturnValue().getPrintedText(), matchesPattern("TestClass@[a-f\\d]+"));
    }

    @Test
    public void shouldNotFailIfToStringCallsTracedMethod() {
        CallRecord root = runSubprocessWithUi(
                new TestSettingsBuilder()
                        .setMainClassName(UserDefinedClassTestCases.class)
                        .setMethodToRecord("returnClassThatCallsSelfInToString")
        );

        assertThat(root.getReturnValue().getPrintedText(), is("ToStringCallsSelf{name='ToStringCallsSelf{name='n1', secondName='s1'}ToStringCallsSelf{name='n1', secondName='s1'}', secondName='ToStringCallsSelf{name='n1', secondName='s1'}/ToStringCallsSelf{name='n1', secondName='s1'}'}"));
    }

    public static class UserDefinedClassTestCases {

        public static void main(String[] args) {
            SafeCaller.call(() -> new UserDefinedClassTestCases().returnInnerClass());
            SafeCaller.call(() -> new UserDefinedClassTestCases().returnClassThatCallsSelfInToString(
                    new UserDefinedClassTestCases.ToStringCallsSelf("n1", "s1"),
                    new UserDefinedClassTestCases.ToStringCallsSelf("n1", "s1")
            ));
        }

        UserDefinedClassTestCases.TestClass returnInnerClass() {
            return new UserDefinedClassTestCases.TestClass();
        }

        UserDefinedClassTestCases.ToStringCallsSelf returnClassThatCallsSelfInToString(UserDefinedClassTestCases.ToStringCallsSelf v1, UserDefinedClassTestCases.ToStringCallsSelf v2) {
            return new UserDefinedClassTestCases.ToStringCallsSelf(v1.toString() + v2.toString(), v1.toString() + "/" + v2.toString());
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

        public class TestClass {

        }
    }
}
