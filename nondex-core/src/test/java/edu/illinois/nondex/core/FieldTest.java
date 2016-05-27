package edu.illinois.nondex.core;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class FieldTest {
    Field myField;

    @Before
    public void setup() throws NoSuchFieldException {
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
