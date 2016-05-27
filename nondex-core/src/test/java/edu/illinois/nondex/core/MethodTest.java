package edu.illinois.nondex.core;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class MethodTest {
    Method myMethod;

    @Before
    public void setup() throws NoSuchMethodException {
        Class clazz = ClassTestHelper.class;
        myMethod = clazz.getMethod("m1", Integer.class);
    }

    @Test
    public void getDeclaredAnnotationsTest() {
        assertThat(myMethod.getDeclaredAnnotations(), not(equalTo(myMethod.getDeclaredAnnotations())));
    }

    @Test
    public void getExceptionTypesTest() {
        assertThat(myMethod.getExceptionTypes(), not(equalTo(myMethod.getExceptionTypes())));
    }

    @Test
    public void getGenericExceptionTypesTest() {
        assertThat(myMethod.getGenericExceptionTypes(), not(equalTo(myMethod.getGenericExceptionTypes())));
    }

    @Test
    public void getParamaterAnnotationsTest() {
        assertThat(myMethod.getParameterAnnotations(), not(equalTo(myMethod.getParameterAnnotations())));
    }
}
