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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QueueTest {
    public AbstractQueue<Integer> queue;

    public QueueTest(AbstractQueue<Integer> queue) {
        this.queue = queue;
    }

    @Parameters
    public static Object[] data() {
        PriorityQueue<Integer> prq = new PriorityQueue<>();
        PriorityBlockingQueue<Integer> prbq = new PriorityBlockingQueue<>();
        return new Object[] {prq, prbq};
    }

    @Before
    public void setUp() {
        queue.clear();
        for (int ind = 0; ind < 10; ind++) {
            queue.add(ind);
        }
    }

    @Test
    public void testShuffling() {
        assertThat(queue.toString(), not(equalTo(queue.toString())));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveWhenEmpty() {
        queue.clear();
        Iterator<Integer> iter = queue.iterator();
        iter.remove();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveWithoutCallingNextBefore() {
        Iterator<Integer> iter = queue.iterator();
        iter.remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextWhenEmpty() {
        queue.clear();
        Iterator<Integer> iter = queue.iterator();
        iter.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTwice() {
        Iterator<Integer> iter = queue.iterator();
        Integer value = iter.next();
        iter.remove();
        assertFalse(queue.contains(value));
        iter.remove();
    }

    @Test
    public void testHasNextWhenEmpty() {
        queue.clear();
        Iterator<Integer> iter = queue.iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    public void testRemove() {
        int size = queue.size();
        Iterator<Integer> iter = queue.iterator();
        for (int i = 0; i < size; i++) {
            assertTrue(iter.hasNext());
            Integer val = iter.next();
            assertTrue(queue.contains(val));
            iter.remove();
            assertEquals(size - i - 1, queue.size());
            assertFalse(queue.contains(val));
        }
        assertEquals(0, queue.size());
    }

    @Test
    public void testToArray() {
        assertThat(queue.toArray(), not(equalTo(queue.toArray())));
    }
}
