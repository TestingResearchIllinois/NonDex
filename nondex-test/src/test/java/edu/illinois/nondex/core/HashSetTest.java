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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class HashSetTest extends AbstractCollectionTest<Set<Integer>> {

    @Override
    protected Set<Integer> createResizedDS() {
        return this.createResizedDS(0, 103);
    }

    @Override
    protected Set<Integer> createResizedDS(int start, int maxSize) {
        Set<Integer> set = new HashSet<>();

        for (int i  = start; i < maxSize; i++) {
            set.add(i);
        }

        for (int i = start + 10; i < maxSize; i++) {
            set.remove(i);
        }

        Assert.assertEquals("the size should be 10", 10, set.size());

        return set;
    }

    @Override
    protected Set<Integer> addRemoveDS(Set<Integer> ds) {
        ds.add(27);
        ds.remove(27);
        return ds;
    }


    @Test
    public void testHashSet() {
        Set<Integer> set = this.createResizedDS(0, 100000);
        Iterator it = set.iterator();
        it.next();
        it.next();
        it.remove();
        it.next();
        Assert.assertEquals("the size should be 9 now", 9, set.size());
        // this is the natural order; 2 should be removed by the iterator remove above
        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "[0, 2, 3, 4, 5, 6, 7, 8, 9]", set.toString());
    }

    @Test
    public void testHashSetParametrized() {
        Set<Integer> set = this.createResizedDS(0, 100000);
        this.assertParameterized(set, set, set.toString());
    }
}
