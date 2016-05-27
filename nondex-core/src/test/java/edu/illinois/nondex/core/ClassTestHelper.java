package edu.illinois.nondex.core;

@TestAnnotation1 @TestAnnotation2 @TestAnnotation3 @TestAnnotation4
public class ClassTestHelper {
    public class InnerOne {}
    public class InnerTwo {}
    public class InnerThree {}
    public class InnerFour {}

    @TestAnnotation1 @TestAnnotation2 @TestAnnotation3 @TestAnnotation4
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

    @TestAnnotation1 @TestAnnotation2 @TestAnnotation3 @TestAnnotation4
    public int m1(@TestAnnotation1 @TestAnnotation2 @TestAnnotation3 @TestAnnotation4 Integer param)
            throws NullPointerException, ArithmeticException, ArrayIndexOutOfBoundsException, ClassCastException {
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
