package edu.illinois.nondex.core;

import org.junit.Test;

import java.text.DateFormat;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class DateFormatTest {
    @Test
    public void getAvailableLocalesTest() {
        assertThat(DateFormat.getAvailableLocales(), not(equalTo(DateFormat.getAvailableLocales())));
    }
}
