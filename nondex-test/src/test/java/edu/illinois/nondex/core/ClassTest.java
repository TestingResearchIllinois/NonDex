/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2015 Owolabi Legunsen
Copyright (c) 2015 Darko Marinov
Copyright (c) 2015 August Shi


Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package edu.illinois.nondex.core;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ClassTest {

    private Class<ClassTestHelper> clazz;

    @Before
    public void setUp() {
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
}
