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

package edu.illinois.nondex.common;

public class ExplorationEntry {

    private int current;
    private int maximum;
    private boolean shouldExplore;

    public ExplorationEntry(final int current, final int maximum, final boolean shouldExplore) {
        this.current = current;
        this.maximum = maximum;
        this.shouldExplore = shouldExplore;
    }

    public int getCurrent() {
        return current;
    }

    public int getMaximum() {
        return maximum;
    }

    public boolean getShouldExplore() {
        return shouldExplore;
    }

    public void setCurrent(final int current) {
        this.current = current;
    }

    public void setMaximum(final int maximum) {
        this.maximum = maximum;
    }

    public void setShouldExplore(final boolean shouldExplore) {
        this.shouldExplore = shouldExplore;
    }
}
