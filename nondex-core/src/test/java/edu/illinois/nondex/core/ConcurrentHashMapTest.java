package edu.illinois.nondex.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

// TODO(gyori): Make this parameterized and run with all
// configurations and over several seeds
public class ConcurrentHashMapTest extends AbstractCollectionTest<ConcurrentHashMap<Integer, Integer>>{

    protected ConcurrentHashMap<Integer, Integer> createResizedDS() {
        return createResizedDS(0, 103);
    }
    
    protected ConcurrentHashMap<Integer, Integer> createResizedDS(int start, int maxSize) {
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

        for (int i  = start; i < maxSize; i++)
            map.put(i, i);

        for (int i = start + 10; i < maxSize; i++)
            map.remove(i);
        
        assertEquals("the size should be 10", 10, map.size());
        
        return map;
    }
    
    protected ConcurrentHashMap<Integer, Integer> addRemoveDS(ConcurrentHashMap<Integer, Integer> ds) {
        ds.put(27, 32);
        ds.remove(27);
        return ds;
    }

    @Test
    public void smokeTest() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS(0, 1000000);
        Iterator it = map.entrySet().iterator();
        it.next();
        it.next();
        it.remove();
        it.next();
        assertEquals("the size should be 9 now", 9, map.size());

        // this is the natural order on most jvms; 2 should be removed by the iterator remove above
        assertNotEquals("You are likely running an unchanged JVM", "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", map.toString());

        String str = map.toString();
        assertNotEquals("You are not running FULL nondex", str, map.toString());
    }
    
    @Test
    public void testKeySet() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS();
        Set<Integer> keySet = map.keySet();

        assertNotEquals("You are likely running an unchanged JVM", "{0, 2, 3, 4, 5, 6, 7, 8, 9}", keySet.toString());
        assertEqualstUnordered("The strings are not a permutation of each other", "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", keySet.toString());

    }
    
    @Test
    public void testKeySetConfig() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS();
        Set<Integer> keySet = map.keySet();

        this.parameterized(map, keySet, keySet.toString());
    }

    @Test
    public void testValues() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS();
        Collection<Integer> values = map.values();

        assertNotEquals("You are likely running an unchanged JVM", "{0, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
    }

    @Test
    public void testValuesParameterized() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS();
        Collection<Integer> values = map.values();

        assertNotEquals("You are likely running an unchanged JVM", "{0, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());
        assertEqualstUnordered("The strings are not a permutation of each other", "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", values.toString());

        String str = values.toString();
        parameterized(map, values, str);
    }

    @Test
    public void testEntrySet() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS();
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();

        assertNotEquals("You are likely running an unchanged JVM", "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", entrySet.toString());
        assertEqualstUnordered("The strings are not a permutation of each other", "{0=0, 1=1, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", entrySet.toString());
    }

    @Test
    public void testEntrySetParameterized() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS();
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();

        String str = entrySet.toString();
        parameterized(map, entrySet, str);
    }

    @Test
    public void testElements() {
        ConcurrentHashMap<Integer, Integer> map = createResizedDS();
        List<Integer> enumerated = Collections.list(map.elements());

        assertNotEquals("You are likely running an unchanged JVM", "{0, 2, 3, 4, 5, 6, 7, 8, 9}", enumerated.toString());
        assertEqualstUnordered("The collection does not containt the elements asserted", 
                "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", enumerated.toString());
    }

    

    
}
