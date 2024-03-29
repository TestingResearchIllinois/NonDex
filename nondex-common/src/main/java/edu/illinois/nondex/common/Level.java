/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2022 Kaiyao Ke
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

public enum Level {
    ALL(Integer.MIN_VALUE, "ALL"),
    FINEST(300, "FINEST"),
    FINER(400, "FINER"),
    FINE(500, "FINE"),
    CONFIG(700, "CONFIG"),
    INFO(800, "INFO"),
    WARNING(900, "WARNING"),
    SEVERE(1000, "SEVERE"),
    OFF(Integer.MAX_VALUE, "OFF");

    private final int severity;

    private Level(int severity, String name) {
        this.severity = severity;
    }

    public static Level parse(String name) {
        return Level.valueOf(name);
    }

    public final String getName() {
        return name();
    }

    public final int intValue() {
        return severity;
    }
}