package edu.illinois.nondex.functionalTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;

import org.junit.Test;

public class DateFormatTest {
    @Test
    public void getAvailableLocalesTest() {
        assertThat(DateFormat.getAvailableLocales(), not(equalTo(DateFormat.getAvailableLocales())));
    }
}
