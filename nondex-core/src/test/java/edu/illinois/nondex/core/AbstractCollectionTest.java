package edu.illinois.nondex.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import edu.illinois.nondex.shuffling.ControlNondeterminism;

public abstract class AbstractCollectionTest<T> {

    abstract protected T createResizedDS();
    abstract protected T createResizedDS(int start, int maxSize);    
    abstract protected T addRemoveDS(T ds);
    
    protected void parameterized(T ds, Object derived, String str) {
        switch (ControlNondeterminism.getConfiguration().mode) {
            case FULL: 
                String s = derived.toString();
                assertNotEquals("FULL is improperly running", str, s);
                assertEqualstUnordered("Does not match permutation", str, s);
                break;
            case ID:
                assertEquals("ID should return the same when collection is unchanged", str, derived.toString());
                this.addRemoveDS(ds);
                assertNotEquals("ID should return different when collection is modified", str, derived.toString());
                break;
            case EQ:
                assertEquals("EQ is improperly running", str, derived.toString());
                this.addRemoveDS(ds);
                assertEquals("EQ should return the same for two equal collections", str, derived.toString());
                break;
            case ONE:
                assertEquals("ONE is improperly running", str, derived.toString());
                break;
        }
    }
    
    protected void assertEqualstUnordered(String msg, String expected, String actual) {
        assertEquals(msg + ": " + expected + " =/= " + actual, expected.length(), actual.length());
        expected = expected.substring(1, expected.length() - 1);
        String[] elems = expected.split(",");
        // TODO(gyori): fix and make this more robust. It does not check duplicates, substrings, etc.
        for (int i = 0; i < elems.length; i++) {
            elems[i] = elems[i].trim();
            assertTrue(msg + ": " + expected + " =/= " + actual, actual.contains(elems[i]));
        }
        
        
    }
}
