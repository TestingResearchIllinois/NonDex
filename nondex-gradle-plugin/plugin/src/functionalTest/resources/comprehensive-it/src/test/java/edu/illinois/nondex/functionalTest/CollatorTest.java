package edu.illinois.nondex.functionalTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.text.Collator;

import org.junit.Test;

public class CollatorTest {
    @Test
    public void getAvailableLocalesTest() {
        assertThat(Collator.getAvailableLocales(), not(equalTo(Collator.getAvailableLocales())));
    }
}
