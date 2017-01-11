package edu.illinois.nondex.it;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.lang.String;
import java.util.HashSet;

public class ExcludedGroupsTest {
    @Test
    @Category(edu.illinois.nondex.it.Module2Ignore.class)
    public void testModuleIgnore() {
        assertTrue(false);
    }

    @Test
    @Category(edu.illinois.NonDexIgnore.class)
    public void testNonDexIgnore() {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < 10; i++) {
            hs.add(i);
        }

        String s = hs.toString();

        assertFalse(s.equals(hs.toString()));
    }
}