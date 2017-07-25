package edu.illinois.nondex.it;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SimpleTest {

    @Test
    public void testSimple() {
        String varValue = System.getProperty("myArgumentForTesting");
        assertEquals("1219", varValue);
        varValue = System.getProperty("throughProperty");
        assertEquals("3423", varValue);
    }
}
