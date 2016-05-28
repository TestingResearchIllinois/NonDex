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

package java.util;

import java.util.HashMap.HashIterator;
import java.util.HashMap.Node;

public class HashIteratorShuffler<K, V> {
    private Iterator<Node<K, V>> iter;
    private HashIterator hashIter;

    public HashIteratorShuffler(HashIterator hi) {
        hashIter = hi;
        List<Node<K, V>> oneOrder = new ArrayList<>();
        while (hashIter.original_hasNext()) {
            oneOrder.add(hashIter.original_nextNode());
        }
        edu.illinois.nondex.shuffling.ControlNondeterminism.shuffle(oneOrder, System.identityHashCode(hi),
                hashIter.expectedModCount, 10);

        iter = oneOrder.iterator();
    }

    public Node<K, V> nextNode(int modCount) {
        if (modCount != hashIter.expectedModCount) {
            throw new ConcurrentModificationException();
        }
        hashIter.current = iter.next();
        return hashIter.current;
    }

    public boolean hasNext() {
        return this.iter.hasNext();
    }
}