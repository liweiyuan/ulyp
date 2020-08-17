package com.ulyp.agent.util;

import net.bytebuddy.description.type.TypeDescription;
import org.h2.jdbc.JdbcConnection;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ByteBuddyTypeTest {

    static class Y {

    }

    static class X {

        @Override
        public String toString() {
            return "X{}";
        }
    }

    @Test
    public void testHasToString() {

        assertTrue(new ByteBuddyType(TypeDescription.ForLoadedType.of(X.class).asGenericType()).hasToStringMethod());

        assertFalse(new ByteBuddyType(TypeDescription.ForLoadedType.of(Y.class).asGenericType()).hasToStringMethod());
    }

    static class BaseClass implements I2, I3 {

    }

    interface I1 {

    }

    interface I2 {

    }

    interface I5 {

    }

    interface I4 extends I5 {

    }

    interface I3 extends I4 {

    }

    static class TestClass extends BaseClass implements I1 {

    }

    @Test
    public void testBaseClassNamesResolve() {
        ByteBuddyType type = new ByteBuddyType(TypeDescription.ForLoadedType.of(TestClass.class).asGenericType());

        Assert.assertEquals(
                new HashSet<String>() {{
                    add("com.ulyp.agent.util.ByteBuddyTypeTest$TestClass");
                    add("com.ulyp.agent.util.ByteBuddyTypeTest$BaseClass");
                }},
                type.getSuperClassesNames()
        );

        Assert.assertEquals(
                new HashSet<String>() {{
                    add("com.ulyp.agent.util.ByteBuddyTypeTest$I1");
                    add("com.ulyp.agent.util.ByteBuddyTypeTest$I2");
                    add("com.ulyp.agent.util.ByteBuddyTypeTest$I3");
                    add("com.ulyp.agent.util.ByteBuddyTypeTest$I4");
                    add("com.ulyp.agent.util.ByteBuddyTypeTest$I5");
                }},
                type.getInterfacesClassesNames()
        );
    }
}