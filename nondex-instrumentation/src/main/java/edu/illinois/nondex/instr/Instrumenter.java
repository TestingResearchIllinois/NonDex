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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;


public final class Instrumenter {
    private static final HashSet<String> classesToShuffle = new HashSet<>();

    static {
        classesToShuffle.add("java/lang/Class.class");
        classesToShuffle.add("java/lang/reflect/Field.class");
        classesToShuffle.add("java/io/File.class");
        classesToShuffle.add("java/text/DateFormatSymbols.class");
        //classesToShuffle.add("java/lang/reflect/Method.class");
    }

    private Instrumenter() {
    }

    private interface Function<T, R> {
        R apply(T param);
    }


    private static void instrumentClass(String className,
                                        Function<ClassVisitor, ClassVisitor> createShuffler,
                                        ZipFile rt, ZipOutputStream outZip) throws IOException {
        InputStream classStream = null;
        try {
            classStream = rt.getInputStream(rt.getEntry(className));
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot find " + className + " are you sure this is a valid rt.jar?");
            e.printStackTrace();
        }
        ClassReader cr = new ClassReader(classStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        // TODO address CheckClassAdapter problem,
        // https://stackoverflow.com/questions/10647290/asm-compute-maxs-not-working-for-basic-test-case
        //CheckClassAdapter ca = new CheckClassAdapter(cw);
        ClassVisitor cv = createShuffler.apply(cw);

        cr.accept(cv, 0);
        byte[] arr = cw.toByteArray();

        ZipEntry zipEntry = new ZipEntry(className);
        outZip.putNextEntry(zipEntry);
        outZip.write(arr, 0, arr.length);
        outZip.closeEntry();
    }

    public static final void instrument(String rtJar, String outJar) throws IOException {
        ZipFile rt = null;
        ZipOutputStream outZip = null;
        try {
            rt = new ZipFile(rtJar);
            outZip = new ZipOutputStream(new FileOutputStream(outJar));
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Are you sure you provided a valid path to your rt.jar?");
            e.printStackTrace();
        }


        for (String cl : classesToShuffle) {
            InputStream clInputStream = null;
            try {
                clInputStream = rt.getInputStream(rt.getEntry(cl));
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "Cannot find " + cl + " are you sure this is a valid rt.jar?");
                e.printStackTrace();
            }

            ClassReader cr = new ClassReader(clInputStream);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            ClassVisitor cv = new ClassVisitorShufflingAdder(cw);

            cr.accept(cv, 0);

            byte[] arr = cw.toByteArray();

            ZipEntry entry = new ZipEntry(cl);
            outZip.putNextEntry(entry);
            outZip.write(arr, 0, arr.length);
            outZip.closeEntry();
        }

        HashIteratorShufflerNodeASMDump hashIterShuffNodeDump = new HashIteratorShufflerNodeASMDump();
        ZipEntry hashIterShuffNodeEntry = new ZipEntry("java/util/HashIteratorShufflerNode.class");
        outZip.putNextEntry(hashIterShuffNodeEntry);
        byte[] hashIterShuffNodeBytes = hashIterShuffNodeDump.dump();
        outZip.write(hashIterShuffNodeBytes, 0, hashIterShuffNodeBytes.length);
        outZip.closeEntry();

        HashIteratorShufflerEntryASMDump hashIterShuffEntryDump = new HashIteratorShufflerEntryASMDump();
        ZipEntry hashIterShuffEntryEntry = new ZipEntry("java/util/HashIteratorShufflerEntry.class");
        outZip.putNextEntry(hashIterShuffEntryEntry);
        byte[] hashIterShuffEntryBytes = hashIterShuffEntryDump.dump();
        outZip.write(hashIterShuffEntryBytes, 0, hashIterShuffEntryBytes.length);
        outZip.closeEntry();

        instrumentClass("java/util/HashMap$HashIterator.class",
                new Function<ClassVisitor, ClassVisitor>() {
                    @Override
                    public ClassVisitor apply(ClassVisitor cv) {
                        return new HashMapShufflingAdder(cv);
                    }
                }, rt, outZip);
        instrumentClass("java/util/concurrent/ConcurrentHashMap$Traverser.class",
                new Function<ClassVisitor, ClassVisitor>() {
                    @Override
                    public ClassVisitor apply(ClassVisitor cv) {
                        return new ConcurrentHashMapShufflingAdder(cv);
                    }
                }, rt, outZip);
        instrumentClass("java/lang/reflect/Method.class",
                new Function<ClassVisitor, ClassVisitor>() {
                    @Override
                    public ClassVisitor apply(ClassVisitor cv) {
                        return new MethodShufflingAdder(cv);
                    }
                }, rt, outZip);

        outZip.close();
    }
}
