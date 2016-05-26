package edu.illinois.nondex.core;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

// TODO(gyori): Make this parameterized and run with all
// configurations and over several seeds
public class HashMapTest extends AbstractCollectionTest<Map<Integer, Integer>>{

    protected Map<Integer, Integer> createResizedDS() {
        return createResizedDS(0, 103);
    }
    
    protected Map<Integer, Integer> createResizedDS(int start, int maxSize) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i  = start; i < maxSize; i++)
            map.put(i, i);

        for (int i = start + 10; i < maxSize; i++)
            map.remove(i);

        assertEquals("the size should be 10", 10, map.size());  
        
        return map;
    }
    
    protected Map<Integer, Integer> addRemoveDS(Map<Integer, Integer> ds) {
        ds.put(27, 32);
        ds.remove(27);
        return ds;
    }
    
    @Test
    public void smokeTest() {
        Map<Integer, Integer> map = createResizedDS(0, 1000000);
        Iterator it = map.entrySet().iterator();
        it.next();
        it.next();
        it.remove();
        it.hasNext();
        assertEquals("the size should be 9 now", 9, map.size());

        // this is the natural order on most jvms; 2 should be removed by the iterator remove above
        String tmp = map.toString();
        assertNotEquals("You are likely running an unchanged JVM", "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", tmp);
        
        String str = map.toString();
        assertNotEquals("You are not running FULL nondex", str, map.toString());
        assertEqualstUnordered("The strings are not a permutation of each other", str, map.toString());
    }
    
    @Test
    public void testKeySet() {
        Map<Integer, Integer> map = createResizedDS();
        Set<Integer> keySet = map.keySet();

        assertNotEquals("You are likely running an unchanged JVM", "{0, 2, 3, 4, 5, 6, 7, 8, 9}", keySet.toString());
        assertEqualstUnordered("The strings are not a permuation of each other", "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", keySet.toString());

    }
    
    @Test
    public void testKeySetConfig() {
        Map<Integer, Integer> map = createResizedDS();
        Set<Integer> keySet = map.keySet();

        this.parameterized(map, keySet, keySet.toString());
    }

    @Test
    public void testValues() {
        Map<Integer, Integer> map = createResizedDS();
        Collection<Integer> values = map.values();

        assertNotEquals("You are likely running an unchanged JVM", "{0, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
        assertEqualstUnordered("The strings are not a permutation of each other", "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
    }

    @Test
    public void testValuesParametrized() {
        Map<Integer, Integer> map = createResizedDS();
        Collection<Integer> values = map.values();

        assertNotEquals("You are likely running an unchanged JVM", "{0, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
        assertEqualstUnordered("The strings are not a permuation of each other", "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());

        String str = values.toString();
        parameterized(map, values, str);
    }

    @Test
    public void testEntrySet() {
        Map<Integer, Integer> map = createResizedDS();
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();

        assertNotEquals("You are likely running an unchanged JVM", "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", entrySet.toString());
        assertEqualstUnordered("The strings are not a permutation of each other", "{0=0, 1=1, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", entrySet.toString());
    }

    @Test
    public void testEntrySetParametrized() {
        Map<Integer, Integer> map = createResizedDS();
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();

        String str = entrySet.toString();
        parameterized(map, entrySet, str);
    }

}
