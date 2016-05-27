package edu.illinois.nondex.core;

import org.junit.Test;

import java.text.DecimalFormatSymbols;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class DecimalFormatSymbolsTest {
    @Test
    public void getAvailableSymbolsTest() {
        assertThat(DecimalFormatSymbols.getAvailableLocales(), not(equalTo(DecimalFormatSymbols.getAvailableLocales())));
    }
}
