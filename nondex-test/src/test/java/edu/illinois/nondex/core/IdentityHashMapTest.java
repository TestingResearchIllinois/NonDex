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

package edu.illinois.nondex.core;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;

import org.junit.Test;

public class IdentityHashMapTest {

    // Author: Chih-Fu Lai (2025)
    // Added test coverage for IdentityHashMap.removeAll iterator behavior
    @Test
    public void testRemoveAllArrayIndexOutOfBounds() {
        for (int i = 0; i < 999; i++) {
            IdentityHashMap<Object, Object> map = new IdentityHashMap<>(4);

            Object k1 = new Object();
            Object k2 = new Object();
            Object k3 = new Object();

            map.put(k1, 1);
            map.put(k2, 2);
            map.put(k3, 3);

            map.keySet().removeAll(Collections.singleton(k2));
        }
    }
}
