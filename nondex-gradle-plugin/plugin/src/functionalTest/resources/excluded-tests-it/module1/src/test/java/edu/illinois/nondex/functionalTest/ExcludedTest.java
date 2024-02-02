package edu.illinois.nondex.functionalTest;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;

public class ExcludedTest {

    @Test
    public void testModuleExclude() {
        assertTrue(false);
    }

    @Test
    public void testNonDexExclude() {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < 10; i++) {
            hs.add(i);
        }

        String s = hs.toString();

        assertFalse(s.equals(hs.toString()));
    }

    @Test
    public void testHashSet() {
        assertTrue(true);
    }
}