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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

// TODO(gyori): Make this parameterized and run with all
// configurations and over several seeds
public class HashMapTest extends AbstractCollectionTest<Map<Integer, Integer>> {

    @Override
    protected Map<Integer, Integer> createResizedDS() {
        return this.createResizedDS(0, 103);
    }

    @Override
    protected Map<Integer, Integer> createResizedDS(int start, int maxSize) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i  = start; i < maxSize; i++) {
            map.put(i, i);
        }

        for (int i = start + 10; i < maxSize; i++) {
            map.remove(i);
        }

        Assert.assertEquals("the size should be 10", 10, map.size());

        return map;
    }

    @Override
    protected Map<Integer, Integer> addRemoveDS(Map<Integer, Integer> ds) {
        ds.put(27, 32);
        ds.remove(27);
        return ds;
    }

    @Test
    public void smokeTest() {
        Map<Integer, Integer> map = this.createResizedDS(0, 1000000);
        Iterator it = map.entrySet().iterator();
        it.next();
        it.next();
        it.remove();
        it.hasNext();
        Assert.assertEquals("the size should be 9 now", 9, map.size());

        // this is the natural order on most jvms; 2 should be removed by the iterator remove above
        String tmp = map.toString();
        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", tmp);

        String str = map.toString();
        Assert.assertNotEquals("You are not running FULL nondex", str, map.toString());
        this.assertEqualstUnordered("The strings are not a permutation of each other", str, map.toString());
    }

    @Test
    public void testKeySet() {
        Map<Integer, Integer> map = this.createResizedDS();
        Set<Integer> keySet = map.keySet();

        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "{0, 2, 3, 4, 5, 6, 7, 8, 9}", keySet.toString());
        this.assertEqualstUnordered("The strings are not a permuation of each other",
                "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", keySet.toString());

    }

    @Test
    public void testKeySetConfig() {
        Map<Integer, Integer> map = this.createResizedDS();
        Set<Integer> keySet = map.keySet();

        this.assertParameterized(map, keySet, keySet.toString());
    }

    @Test
    public void testValues() {
        Map<Integer, Integer> map = this.createResizedDS();
        Collection<Integer> values = map.values();

        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "{0, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
        this.assertEqualstUnordered("The strings are not a permutation of each other",
                "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
    }

    @Test
    public void testValuesParametrized() {
        Map<Integer, Integer> map = this.createResizedDS();
        Collection<Integer> values = map.values();

        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "{0, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
        this.assertEqualstUnordered("The strings are not a permuation of each other",
                "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());

        String str = values.toString();
        this.assertParameterized(map, values, str);
    }

    @Test
    public void testEntrySet() {
        Map<Integer, Integer> map = this.createResizedDS();
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();

        Assert.assertNotEquals("You are likely running an unchanged JVM",
                "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", entrySet.toString());
        this.assertEqualstUnordered("The strings are not a permutation of each other",
                "{0=0, 1=1, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", entrySet.toString());
    }

    @Test
    public void testEntrySetParametrized() {
        Map<Integer, Integer> map = this.createResizedDS();
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();

        String str = entrySet.toString();
        this.assertParameterized(map, entrySet, str);
    }
}
