package com.ulyp.agent.util;

import com.ulyp.core.printers.TypeTrait;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class ByteBuddyTypeInfoTest {

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

        assertTrue(new ByteBuddyTypeInfo(TypeDescription.ForLoadedType.of(X.class).asGenericType()).hasToStringMethod());

        assertFalse(new ByteBuddyTypeInfo(TypeDescription.ForLoadedType.of(Y.class).asGenericType()).hasToStringMethod());
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

    public static <T> void takesGenericArray(T[] array) {

    }

    @Test
    public void testTakesGenericArray() throws NoSuchMethodException {
        MethodDescription.ForLoadedMethod.InDefinedShape.ForLoadedMethod method = new MethodDescription.ForLoadedMethod.InDefinedShape.ForLoadedMethod(
                this.getClass().getDeclaredMethod("takesGenericArray", Object[].class)
        );
        TypeDescription.Generic firstArgType = method.getParameters().asTypeList().get(0);


        ByteBuddyTypeInfo byteBuddyTypeInfo = new ByteBuddyTypeInfo(firstArgType);


        assertThat(byteBuddyTypeInfo.getTraits(), Matchers.hasItem(TypeTrait.NON_PRIMITIVE_ARRAY));
    }

    public static void takesObjectArray(Object[] array) {

    }

    @Test
    public void testTakesObjectArray() throws NoSuchMethodException {
        MethodDescription.ForLoadedMethod.InDefinedShape.ForLoadedMethod method = new MethodDescription.ForLoadedMethod.InDefinedShape.ForLoadedMethod(
                this.getClass().getDeclaredMethod("takesObjectArray", Object[].class)
        );
        TypeDescription.Generic firstArgType = method.getParameters().asTypeList().get(0);


        ByteBuddyTypeInfo byteBuddyTypeInfo = new ByteBuddyTypeInfo(firstArgType);


        assertThat(byteBuddyTypeInfo.getTraits(), Matchers.hasItem(TypeTrait.NON_PRIMITIVE_ARRAY));
    }

    public static void takesClass(Class<?> x) {

    }

    @Test
    public void testClassTypeTraits() throws NoSuchMethodException {
        MethodDescription.ForLoadedMethod.InDefinedShape.ForLoadedMethod method = new MethodDescription.ForLoadedMethod.InDefinedShape.ForLoadedMethod(
                this.getClass().getDeclaredMethod("takesClass", Class.class)
        );

        TypeDescription.Generic firstArgType = method.getParameters().asTypeList().get(0);

        ByteBuddyTypeInfo byteBuddyTypeInfo = new ByteBuddyTypeInfo(firstArgType);

        assertThat(byteBuddyTypeInfo.getTraits(), Matchers.hasItem(TypeTrait.CLASS_OBJECT));
    }

    @Test
    public void testNumberTypeTraits() {

        assertThat(new ByteBuddyTypeInfo(Integer.class).getTraits(), Matchers.hasItem(TypeTrait.NUMBER));

        assertThat(new ByteBuddyTypeInfo(Long.class).getTraits(), Matchers.hasItem(TypeTrait.NUMBER));
    }

    @Test
    public void testBaseClassNamesResolve() {
        ByteBuddyTypeInfo type = new ByteBuddyTypeInfo(TestClass.class);

        Assert.assertEquals(
                new HashSet<String>() {{
                    add("com.ulyp.agent.util.ByteBuddyTypeInfoTest$TestClass");
                    add("com.ulyp.agent.util.ByteBuddyTypeInfoTest$BaseClass");
                }},
                type.getSuperClassesNames()
        );

        Assert.assertEquals(
                new HashSet<String>() {{
                    add("com.ulyp.agent.util.ByteBuddyTypeInfoTest$I1");
                    add("com.ulyp.agent.util.ByteBuddyTypeInfoTest$I2");
                    add("com.ulyp.agent.util.ByteBuddyTypeInfoTest$I3");
                    add("com.ulyp.agent.util.ByteBuddyTypeInfoTest$I4");
                    add("com.ulyp.agent.util.ByteBuddyTypeInfoTest$I5");
                }},
                type.getInterfacesClassesNames()
        );
    }
}