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

package edu.illinois.nondex.core;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.junit.Before;
import org.junit.Test;

public class PriorityQueueTest {

    private PriorityQueue<Integer> prq;

    @Before
    public void setUp() {
        prq = new PriorityQueue<Integer>();
        for (int ind = 0; ind < 10; ind++) {
            prq.add(ind);
        }
    }

    @Test
    public void iteratorTest() {
        Iterator<Integer> it1 = prq.iterator();
        List<Integer> list1 = new ArrayList<>();

        while (it1.hasNext()) {
            list1.add(it1.next());
        }

        Iterator<Integer> it2 = prq.iterator();
        List<Integer> list2 = new ArrayList<>();

        while (it2.hasNext()) {
            list2.add(it2.next());
        }

        assertThat(list1.size(), equalTo(list2.size()));
        assertThat(list1, not(equalTo(list2)));
    }

    @Test
    public void toArrayTest() {
        assertThat(prq.toArray(), not(equalTo(prq.toArray())));
    }

    @Test
    public void toArrayArgTest() {
        Integer[] list1 = new Integer[10];
        Integer[] list2 = new Integer[10];

        prq.toArray(list1);
        prq.toArray(list2);

        assertThat(list1, not(equalTo(list2)));
    }
}
