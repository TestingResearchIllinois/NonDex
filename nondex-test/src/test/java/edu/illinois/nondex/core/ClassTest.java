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

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class ClassTest {

    @Test
    public void smokeTest() {
        Class<HashMap> clazz = HashMap.class;
        Assert.assertNotEquals(clazz.getClasses(), clazz.getClasses());
        Assert.assertNotEquals(clazz.getFields(), clazz.getFields());
        Assert.assertNotEquals(clazz.getDeclaredFields(), clazz.getDeclaredFields());
        Assert.assertNotEquals(clazz.getConstructors(), clazz.getConstructors());
        Assert.assertNotEquals(clazz.getDeclaredConstructors(), clazz.getDeclaredConstructors());
        Assert.assertNotEquals(clazz.getMethods(), clazz.getMethods());
        Assert.assertNotEquals(clazz.getDeclaredMethods(), clazz.getDeclaredMethods());
        //This class has no annotations, likely
        //assertNotEquals(clazz.getAnnotations(), clazz.getAnnotations());
        //assertNotEquals(clazz.getDeclaredAnnotations(), clazz.getDeclaredAnnotations());
    }
}