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

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;

import org.junit.Assert;
import org.junit.Test;


public class HashedMapTest extends AbstractCollectionTest<HashedMap<Integer,Integer>> {

    @Override
    protected HashedMap<Integer,Integer> createResizedDS() {
        return this.createResizedDS(0, 103);
    }

    @Override
    protected HashedMap<Integer,Integer> createResizedDS(int start, int maxSize) {
        HashedMap<Integer,Integer> map = new HashedMap();

        for (int i  = start; i < maxSize; i++) {
            map.put(i,i);
        }

        for (int i = start + 10; i < maxSize; i++) {
            map.remove(i);
        }

        Assert.assertEquals("the size should be 10", 10, map.size());

        return map;
    }

    @Override
    protected HashedMap<Integer,Integer> addRemoveDS(HashedMap<Integer,Integer> ds) {
        ds.put(27,32);
        ds.remove(27);
        return ds;
    }

    @Test
    public void testKeySet() {
        HashedMap<Integer, Integer> map = this.createResizedDS();
        Set<Integer> keySet = map.keySet();

        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "[1, 3, 2, 7, 6, 4, 5, 8, 9, 0]", keySet.toString());
        this.assertEqualstUnordered("The strings are not a permuation of each other",
                "[1, 3, 2, 7, 6, 4, 5, 8, 9, 0]", keySet.toString());

    }

    @Test
    public void testEntrySet() {
        HashedMap<Integer,Integer> map = this.createResizedDS();
        Set<Entry<Integer,Integer>> entrySet = map.entrySet();

        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "[1=1, 3=3, 2=2, 7=7, 6=6, 4=4, 5=5, 8=8, 9=9, 0=0]", entrySet.toString());
        this.assertEqualstUnordered("The strings are not a permutation of each other",
                "[1=1, 3=3, 2=2, 7=7, 6=6, 4=4, 5=5, 8=8, 9=9, 0=0]", entrySet.toString());
    }

    @Test
    public void testValues() {
        HashedMap<Integer, Integer> map = this.createResizedDS();
        Collection<Integer> values = map.values();

        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "[1, 3, 2, 7, 6, 4, 5, 8, 9, 0]", values.toString());
        this.assertEqualstUnordered("The strings are not a permutation of each other",
                "[1, 3, 2, 7, 6, 4, 5, 8, 9, 0]", values.toString());
    }

}
