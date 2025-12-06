/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2022 Kaiyao Ke
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

import java.util.Arrays;

import edu.illinois.nondex.shuffling.ControlNondeterminism;

import org.junit.Assert;

public abstract class AbstractCollectionTest<T> {

    protected abstract T createResizedDS();

    protected abstract T createResizedDS(int start, int maxSize);

    protected abstract T addRemoveDS(T ds);

    protected void assertParameterized(T ds, Object derived, String str) {
        switch (ControlNondeterminism.getConfiguration().mode) {
            case FULL:
                String tempStr = derived.toString();
                Assert.assertNotEquals("FULL is improperly running", str, tempStr);
                this.assertEqualsUnordered("Does not match permutation", str, tempStr);
                break;
            case ONE:
                Assert.assertEquals("ONE is improperly running", str, derived.toString());
                break;
            default:
                break;
        }
    }

    private String[] trimAndSplitStrings(String msg) {
        String trimmed = msg.substring(1, msg.length() - 1);
        String[] elems = trimmed.split(", ");
        return elems;
    }

    protected void assertEqualsUnordered(String msg, String expected, String actual) {
        String trimmed = expected.substring(1, expected.length() - 1);
        String[] actualTokenized = this.trimAndSplitStrings(actual);
        String[] expectedTokenized = this.trimAndSplitStrings(expected);

        Arrays.sort(actualTokenized);
        Arrays.sort(expectedTokenized);
        Assert.assertArrayEquals(msg + ": " + trimmed + " =/= " + actual, expectedTokenized, actualTokenized);
    }
}
