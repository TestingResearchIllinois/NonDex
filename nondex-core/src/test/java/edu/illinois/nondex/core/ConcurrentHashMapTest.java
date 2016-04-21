package edu.illinois.nondex.core;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

// TODO(gyori): Make this parameterized and run with all
// configurations and over several seeds
public class ConcurrentHashMapTest {

    @Test
    public void smokeTest() {
        Map<Integer, Integer> map = new ConcurrentHashMap<>();

        for (int i  = 0; i<1000000; i++)
            map.put(i, i);

        for (int i = 10; i<1000000; i++)
            assertEquals("remove does not work properly", i, (Object)map.remove(i));

        Iterator it = map.entrySet().iterator();

        it.next();
        it.next();
        it.remove();
        it.next();


        // this is the natural order; 2 should be removed by the iterator remove above
        assertNotEquals("You are likely running an unchanged JVM", "{0=0, 2=2, 3=3, 4=4, 5=5, 6=6, 7=7, 8=8, 9=9}", map.toString());

        //assertEquals("{2=2, 4=4, 0=0, 5=5, 1=1, 9=9, 6=6, 3=3, 8=8}", map.toString());

        for (int i = 0; i < 10; i++)
            if (map.containsKey(i))
                assertEquals("get does not work as it should", i, (Object)map.get(i));


    }
}
