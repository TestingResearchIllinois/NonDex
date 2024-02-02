package edu.illinois.nondex.functionalTest;

import edu.illinois.nondex.shuffling.ControlNondeterminism;

import org.junit.Assert;

public abstract class AbstractCollectionTest<T> {

    protected abstract T createResizedDS();

    protected abstract T createResizedDS(int start, int maxSize);

    protected abstract T addRemoveDS(T ds);

    protected void assertParameterized(T ds, Object derived, String str) {
        switch (ControlNondeterminism.getConfiguration().mode) {
            case FULL:
                String tempStr = derived.toString();
                Assert.assertNotEquals("FULL is improperly running", str, tempStr);
                this.assertEqualstUnordered("Does not match permutation", str, tempStr);
                break;
            case ONE:
                Assert.assertEquals("ONE is improperly running", str, derived.toString());
                break;
            default:
                break;
        }
    }

    protected void assertEqualstUnordered(String msg, String expected, String actual) {
        Assert.assertEquals(msg + ": " + expected + " =/= " + actual, expected.length(), actual.length());
        String trimmed = expected.substring(1, expected.length() - 1);
        String[] elems = trimmed.split(",");
        // TODO(gyori): fix and make this more robust. It does not check duplicates, substrings, etc.
        for (int i = 0; i < elems.length; i++) {
            elems[i] = elems[i].trim();
            Assert.assertTrue(msg + ": " + trimmed + " =/= " + actual, actual.contains(elems[i]));
        }
    }
}
