package edu.illinois.nondex.functionalTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

public class FieldTest {
    private Field myField;

    @Before
    public void setUp() throws NoSuchFieldException {
        Class clazz = ClassTestHelper.class;
        myField = clazz.getField("field1");
    }

    @Test
    public void getDeclaredAnnotationsTest() {
        assertThat(myField.getDeclaredAnnotations(), not(equalTo(myField.getDeclaredAnnotations())));
    }

    @Test
    public void getAnnotationsTest() {
        assertThat(myField.getAnnotations(), not(equalTo(myField.getAnnotations())));
    }
}
