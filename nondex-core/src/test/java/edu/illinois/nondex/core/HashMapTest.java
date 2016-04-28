package edu.illinois.nondex.core;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

// TODO(gyori): Make this parameterized and run with all
// configurations and over several seeds
public class HashMapTest {

    @Test
    public void smokeTest() {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i  = 0; i<1000000; i++)
            map.put(i, i);

        for (int i = 10; i<1000000; i++)
            map.remove(i);

        Iterator it = map.entrySet().iterator();
        it.next();
        it.next();
        it.remove();
        it.next();


        // this is the natural order; 2 should be removed by the iterator remove above
        assertNotEquals("You are likely running an unchanged JVM", "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", map.toString());

        String str = map.toString();
        assertNotEquals("You are not running FULL nondex", str, map.toString());
        
        //assertEquals("{6=6, 8=8, 2=2, 5=5, 7=7, 0=0, 9=9, 3=3, 1=1}", map.toString());
    }

    @Test
    public void testHashSet() {
        Set s = new HashSet();

        for (int i  = 0; i<1000000; i++)
            s.add(i);

        for (int i = 10; i<1000000; i++)
            s.remove(i);

        Iterator it = s.iterator();
        it.next();
        it.next();
        it.remove();
        it.next();

        // this is the natural order; 2 should be removed by the iterator remove above
        assertNotEquals("You are likely running an unchanged JVM", "[0, 2, 3, 4, 5, 6, 7, 8, 9]", s.toString());

        String str = s.toString();
        assertNotEquals("You are not running FULL nondex", str, s.toString());
        //assertEquals("[6, 8, 2, 5, 7, 0, 9, 3, 1]", s.toString());
    }
}
