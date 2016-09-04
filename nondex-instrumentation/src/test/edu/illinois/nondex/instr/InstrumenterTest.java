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

package edu.illinois.nondex.instr;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

public class InstrumenterTest {
    @Test
    public void emptyZipTest() {
        try {
            Instrumenter.instrument(Paths.get("resources", "empty.jar"), Paths.get("resources", "emptyOut.jar"));
            fail("Expected an IOException to be thrown");
        } catch (IOException exc) {
            assertThat(exc.getMessage(), is("zip file is empty"));
        }
    }

    @Test
    public void nonexistantZipTest() {
        try {
            Instrumenter.instrument(Paths.get("resources", "doesnotexist.jar").toString(), Paths.get("resources", "doesnotexistOut.jar").toString());
            fail("Expected an IOException to be thrown");
        } catch (IOException exc) {
            assertThat(exc.getMessage(), is("resources/doesnotexist.jar (No such file or directory)"));
        }
    }
}
