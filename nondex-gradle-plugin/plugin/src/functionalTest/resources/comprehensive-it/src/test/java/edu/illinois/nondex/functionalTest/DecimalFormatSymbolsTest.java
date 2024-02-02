package edu.illinois.nondex.functionalTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.text.DecimalFormatSymbols;

import org.junit.Test;

public class DecimalFormatSymbolsTest {
    @Test
    public void getAvailableSymbolsTest() {
        assertThat(DecimalFormatSymbols.getAvailableLocales(), not(equalTo(DecimalFormatSymbols.getAvailableLocales())));
    }
}
