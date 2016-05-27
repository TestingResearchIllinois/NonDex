package edu.illinois.nondex.core;

import org.junit.Test;

import java.text.DateFormatSymbols;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class DateFormatSymbolsTest {
    @Test
    public void getAvailableLocalesTest() {
        assertThat(DateFormatSymbols.getAvailableLocales(), not(equalTo(DateFormatSymbols.getAvailableLocales())));
    }

    @Test
    public void getZoneStringsTest() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        assertThat(dfs.getZoneStrings(), not(equalTo(dfs.getZoneStrings())));
    }
}
