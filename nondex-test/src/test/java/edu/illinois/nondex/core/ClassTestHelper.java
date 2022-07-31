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

@TestAnnotation1
@TestAnnotation2
@TestAnnotation3
@TestAnnotation4
@TestAnnotation5
@TestAnnotation6
@TestAnnotation7
@TestAnnotation8
public class ClassTestHelper {

    public class InnerOne {

    }

    public class InnerTwo {

    }

    public class InnerThree {

    }

    public class InnerFour {

    }

    @TestAnnotation1
    @TestAnnotation2
    @TestAnnotation3
    @TestAnnotation4
    @TestAnnotation5
    @TestAnnotation6
    @TestAnnotation7
    @TestAnnotation8
    public Integer field1;

    public Integer field2;
    public Integer field3;
    public Integer field4;

    public ClassTestHelper() {

    }

    public ClassTestHelper(Integer f1) {
        field1 = f1;
    }

    public ClassTestHelper(Integer f1, Integer f2) {
        field1 = f1;
        field2 = f2;
    }

    public ClassTestHelper(Integer f1, Integer f2, Integer f3) {
        field1 = f1;
        field2 = f2;
        field3 = f3;
    }

    public ClassTestHelper(Integer f1, Integer f2, Integer f3, Integer f4) {
        field1 = f1;
        field2 = f2;
        field3 = f3;
        field4 = f4;
    }

    @TestAnnotation1
    @TestAnnotation2
    @TestAnnotation3
    @TestAnnotation4
    @TestAnnotation5
    @TestAnnotation6
    @TestAnnotation7
    @TestAnnotation8
    public int m1(@TestAnnotation1 @TestAnnotation2 @TestAnnotation3 @TestAnnotation4 Integer param)
        throws NullPointerException, ArithmeticException, ArrayIndexOutOfBoundsException, ClassCastException, Exception {
        return param;
    }

    public int m2() {
        return 2;
    }

    public int m3() {
        return 3;
    }

    public int m4() {
        return 4;
    }
}
