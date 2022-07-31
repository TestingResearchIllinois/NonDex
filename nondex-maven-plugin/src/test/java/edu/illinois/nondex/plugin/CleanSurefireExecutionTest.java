/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2015 Owolabi Legunsen
Copyright (c) 2015 Darko Marinov
Copyright (c) 2015 August Shi


Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package edu.illinois.nondex.plugin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CleanSurefireExecutionTest {

    @Test
    public void testReplacementWorks() {
        String result = CleanSurefireExecution.sanitizeAndRemoveEnvironmentVars("${somestuff}");
        assertEquals("", result);
    }

    @Test
    public void testReplacementMultiple() {
        String result = CleanSurefireExecution.sanitizeAndRemoveEnvironmentVars("${some-stuff} ${some.MoreStuff} \n${a}");
        assertEquals("", result);
    }

    @Test
    public void testNothingChanges() {
        String result = CleanSurefireExecution.sanitizeAndRemoveEnvironmentVars("several$ }things{ in he}re");
        assertEquals("several$ }things{ in he}re", result);
    }

    @Test
    public void testMixed() {
        String result = CleanSurefireExecution.sanitizeAndRemoveEnvironmentVars("${somestuff} and other stuff ${and}}");
        assertEquals("and other stuff }", result);
    }
}
