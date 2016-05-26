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

import edu.illinois.nondex.shuffling.ControlNondeterminism;

import org.junit.Assert;

public abstract class AbstractCollectionTest<T> {

    protected abstract T createResizedDS();

    protected abstract T createResizedDS(int start, int maxSize);

    protected abstract T addRemoveDS(T ds);

    protected void parameterized(T ds, Object derived, String str) {
        switch (ControlNondeterminism.getConfiguration().mode) {
            case FULL:
                String tempStr = derived.toString();
                Assert.assertNotEquals("FULL is improperly running", str, tempStr);
                this.assertEqualstUnordered("Does not match permutation", str, tempStr);
                break;
            case ID:
                Assert.assertEquals("ID should return the same when collection is unchanged", str, derived.toString());
                this.addRemoveDS(ds);
                Assert.assertNotEquals("ID should return different when collection is modified", str, derived.toString());
                break;
            case EQ:
                Assert.assertEquals("EQ is improperly running", str, derived.toString());
                this.addRemoveDS(ds);
                Assert.assertEquals("EQ should return the same for two equal collections", str, derived.toString());
                break;
            case ONE:
                Assert.assertEquals("ONE is improperly running", str, derived.toString());
                break;
            default:
                break;
        }
    }

    protected void assertEqualstUnordered(String msg, String expected, String actual) {
        Assert.assertEquals(msg + ": " + expected + " =/= " + actual, expected.length(), actual.length());
        expected = expected.substring(1, expected.length() - 1);
        String[] elems = expected.split(",");
        // TODO(gyori): fix and make this more robust. It does not check duplicates, substrings, etc.
        for (int i = 0; i < elems.length; i++) {
            elems[i] = elems[i].trim();
            Assert.assertTrue(msg + ": " + expected + " =/= " + actual, actual.contains(elems[i]));
        }


    }
}
