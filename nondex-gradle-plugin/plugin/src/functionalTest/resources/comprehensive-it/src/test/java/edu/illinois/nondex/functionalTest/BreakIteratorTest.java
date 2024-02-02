package edu.illinois.nondex.functionalTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.text.BreakIterator;

import org.junit.Test;

public class BreakIteratorTest {

    @Test
    public void getAvailableLocalesTest() {
        assertThat(BreakIterator.getAvailableLocales(), not(equalTo(BreakIterator.getAvailableLocales())));
    }
}
