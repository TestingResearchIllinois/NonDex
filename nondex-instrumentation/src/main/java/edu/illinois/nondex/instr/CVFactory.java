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

import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipFile;

import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Level;

import org.objectweb.asm.ClassVisitor;

public class CVFactory {
    public static ClassVisitor construct(ClassVisitor cv, String clzToInstrument, ZipFile rt)
            throws NoSuchAlgorithmException {
        if (clzToInstrument.equals(Instrumenter.concurrentHashMapName)) {
            return new ConcurrentHashMapShufflingAdder(cv);
        } else if (clzToInstrument.equals(Instrumenter.hashMapName)) {
            if (rt.getEntry("java/util/HashMap$Node.class") != null) {
                return new HashMapShufflingAdder(cv, "Node");
            } else if (rt.getEntry("java/util/HashMap$Entry.class") != null) {
                return new HashMapShufflingAdder(cv, "Entry");
            }
        } else if (clzToInstrument.equals(Instrumenter.weakHashMapName)) {
            return new WeakHashMapShufflingAdder(cv);
        } else if (clzToInstrument.equals(Instrumenter.identityHashMapName)) {
            return new IdentityHashMapShufflingAdder(cv);
        } else if (clzToInstrument.equals(Instrumenter.methodName)) {
            return new MethodShufflingAdder(cv);
        } else if (clzToInstrument.equals(Instrumenter.priorityQueueName)) {
            return new PriorityQueueShufflingAdder(cv);
        } else if (clzToInstrument.equals(Instrumenter.priorityBlockingQueueName)) {
            return new PriorityBlockingQueueShufflingAdder(cv);
        } else {
            Logger.getGlobal().log(Level.CONFIG, "Trying to construct CV for " + clzToInstrument);
            throw new NoSuchAlgorithmException();
        }
        return null;
    }
}
