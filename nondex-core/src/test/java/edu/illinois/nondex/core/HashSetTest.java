package edu.illinois.nondex.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class HashSetTest extends AbstractCollectionTest<Set<Integer>>{

    protected Set<Integer> createResizedDS() {
        return createResizedDS(0, 103);
    }
    
    protected Set<Integer> createResizedDS(int start, int maxSize) {
        Set<Integer> set = new HashSet<>();

        for (int i  = start; i < maxSize; i++)
            set.add(i);

        for (int i = start + 10; i < maxSize; i++)
            set.remove(i);

        assertEquals("the size should be 10", 10, set.size());
        
        return set;
    }
    
    protected Set<Integer> addRemoveDS(Set<Integer> ds) {
        ds.add(27);
        ds.remove(27);
        return ds;
    }


    @Test
    public void testHashSet() {
        Set<Integer> s = createResizedDS(0, 100000);
        Iterator it = s.iterator();
        it.next();
        it.next();
        it.remove();
        it.next();
        assertEquals("the size should be 9 now", 9, s.size());
        // this is the natural order; 2 should be removed by the iterator remove above
        assertNotEquals("You are likely running an unchanged JVM", "[0, 2, 3, 4, 5, 6, 7, 8, 9]", s.toString());
    }
    
    @Test
    public void testHashSetParametrized() {
        Set<Integer> s = createResizedDS(0, 100000); 
        this.parameterized(s, s, s.toString());
    }
}
