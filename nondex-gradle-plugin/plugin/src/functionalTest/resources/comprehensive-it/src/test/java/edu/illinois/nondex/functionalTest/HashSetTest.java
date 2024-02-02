package edu.illinois.nondex.functionalTest;

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
