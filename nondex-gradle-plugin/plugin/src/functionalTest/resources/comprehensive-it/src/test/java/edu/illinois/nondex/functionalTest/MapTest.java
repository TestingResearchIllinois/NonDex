package edu.illinois.nondex.functionalTest;

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
        WeakHashMap<Integer, Integer> whm = new WeakHashMap<>();
        IdentityHashMap<Integer, Integer> ihm = new IdentityHashMap<>();
        ConcurrentHashMap<Integer, Integer> chm = new ConcurrentHashMap<>();
        return new Object[] {hm, whm, ihm, chm};
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

    @Test(expected = IllegalStateException.class)
    public void testRemoveWhenNotEmpty() {
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        iter.remove();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTwice() {
        int size = map.size();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        assertTrue(iter.hasNext());
        Entry<K, V> entry = iter.next();
        K key = entry.getKey();
        assertTrue(map.entrySet().contains(entry));
        iter.remove();
        assertEquals(size - 1, map.size());
        assertFalse(map.containsKey(key));
        iter.remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextWhenEmpty() {
        map.clear();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        iter.next();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testModify() {
        //Since concurrentHashMap should not throw the concurrentModificationException,
        //here we throw the exception in order to skip this test case for concurrentHashMap
        if (map instanceof ConcurrentHashMap<?,?>) {
            throw new ConcurrentModificationException();
        }

        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        map.clear();
        iter.next();
    }

    @Test
    public void testHasNextWhenEmpty() {
        map.clear();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    public void testRemove() {
        int size = map.size();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        for (int i = 0; i < size; i++) {
            assertTrue(iter.hasNext());
            Entry<K, V> entry = iter.next();
            K key = entry.getKey();
            assertTrue(map.entrySet().contains(entry));
            iter.remove();
            assertEquals(size - i - 1, map.size());
            assertFalse(map.containsKey(key));
        }
        assertEquals(0, map.size());
    }

    @Test
    public void testShuffling() {
        assertThat(map.toString(), not(equalTo(map.toString())));
    }
}
