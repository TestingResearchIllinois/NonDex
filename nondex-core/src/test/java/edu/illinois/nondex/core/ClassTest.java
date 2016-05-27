package edu.illinois.nondex.core;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ClassTest {

    Class<ClassTestHelper> clazz;

    @Before
    public void setup() {
        // TODO: pick a class with annotations and test:
        // clazz.getAnnotations();
        // clazz.getDeclaredAnnotations();
        clazz = ClassTestHelper.class;
    }

    @Test
    public void getClassesTest() {
        assertThat(clazz.getClasses(), not(equalTo(clazz.getClasses())));
    }

    @Test
    public void getFieldsTest() {
        assertThat(clazz.getFields(), not(equalTo(clazz.getFields())));
    }

    @Test
    public void getDeclaredFieldsTest() {
        assertThat(clazz.getDeclaredFields(), not(equalTo(clazz.getDeclaredFields())));
    }

    @Test
    public void getConstructorsTest() throws NoSuchMethodException {
        assertThat(clazz.getConstructors(), not(equalTo(clazz.getConstructors())));
    }

    @Test
    public void getDeclaredConstructorsTest() {
        assertThat(clazz.getDeclaredConstructors(), not(equalTo(clazz.getDeclaredConstructors())));
    }

    @Test
    public void getMethodsTest() {
        assertThat(clazz.getMethods(), not(equalTo(clazz.getMethods())));
    }

    @Test
    public void getDeclaredMethodsTest() {
        assertThat(clazz.getDeclaredMethods(), not(equalTo(clazz.getDeclaredMethods())));
    }

    @Test
    public void getAnnotationsTest() {
        assertThat(clazz.getAnnotations(), not(equalTo(clazz.getAnnotations())));
    }

    @Test
    public void getDeclaredAnnotationsTest() {
        assertThat(clazz.getDeclaredAnnotations(), not(equalTo(clazz.getDeclaredAnnotations())));
    }
}
