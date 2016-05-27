package edu.illinois.nondex.core;

import org.junit.Test;

import java.text.Collator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class CollatorTest {
    @Test
    public void getAvailableLocalesTest() {
        assertThat(Collator.getAvailableLocales(), not(equalTo(Collator.getAvailableLocales())));
    }
}
