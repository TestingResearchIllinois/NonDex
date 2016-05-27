package edu.illinois.nondex.core;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import org.junit.Test;

import java.text.BreakIterator;
import java.util.Locale;

import static org.junit.Assert.assertThat;

public class BreakIteratorTest {

    @Test
    public void getAvailableLocalesTest() {
        assertThat(BreakIterator.getAvailableLocales(), not(equalTo(BreakIterator.getAvailableLocales())));
    }
}
