package edu.illinois.nondex.functionalTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.text.DateFormatSymbols;
import java.util.Arrays;

import org.junit.Test;

public class DateFormatSymbolsTest {
    @Test
    public void getAvailableLocalesTest() {
        assertThat(DateFormatSymbols.getAvailableLocales(), not(equalTo(DateFormatSymbols.getAvailableLocales())));
    }

    @Test
    public void getZoneStringsTest() {

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[][] result = dfs.getZoneStrings();
        for (int i = 0; i < 10; i++) {
            if (!Arrays.deepEquals(result, dfs.getZoneStrings())) {
                return;
            }
        }
        fail("getZoneStrings did not extend in 10 tries; something is likely fishy.");
    }
}
