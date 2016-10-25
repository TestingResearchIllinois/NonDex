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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MapTest<K, V> {
    public Map<K, V> map;

    public MapTest(Map<K, V> map) {
        this.map = map;
    }

    @Parameters
    public static Object[] data() {
        HashMap<Integer, Integer> hm = new HashMap<>();
//        WeakHashMap<Integer, Integer> whm = new WeakHashMap<>();
//        IdentityHashMap<Integer, Integer> ihm = new IdentityHashMap<>();
//        ConcurrentHashMap<Integer, Integer> chm = new ConcurrentHashMap<>();
        return new Object[] {hm};
    }

    @Before
    public void setUp() {
        for (int i = 0; i < 10; i++) {
            ((Map<Integer, Integer>) map).put(i, i);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveWhenEmpty() {
        map.clear();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        iter.remove();
    }

    @Test
    public void testHasNextWhenEmpty() {
        map.clear();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        assertFalse(iter.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextWhenEmpty() {
        map.clear();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        iter.next();
    }

    @Test
    public void testRemove() {
        int size = map.size();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        assertTrue(iter.hasNext());
        Entry<K, V> entry = iter.next();
        K key = entry.getKey();
        assertTrue(map.entrySet().contains(entry));
        iter.remove();
        assertEquals(size - 1, map.size());
        assertFalse(map.containsKey(key));
//        assertFalse(map.entrySet().contains(entry));
    }

    @Test
    public void testShuffling() {
        assertThat(map.toString(), not(equalTo(map.toString())));
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testModify() {
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        map.clear();
        iter.next();
    }
}
