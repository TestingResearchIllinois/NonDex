package edu.illinois.nondex.it;

public class ExcludedGroupsTest {
    @Test
    @Category(edu.illinois.nondex.it.GlobalIgnore.class)
    public void testGlobalIgnore() {
        assertTrue(false);
    }
}
