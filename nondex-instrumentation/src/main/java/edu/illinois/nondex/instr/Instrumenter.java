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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
    private static final HashSet<String> classesToCopy = new HashSet<>();
    private static final HashSet<String> specialClassesToShuffle = new HashSet<>();
    private static final HashSet<String> specialClassesToCopy = new HashSet<>();

    private static String hashMapName = "java/util/HashMap$HashIterator.class";
    private static String concurrentHashMapName = "java/util/concurrent/ConcurrentHashMap$Traverser.class";
    private static String methodName = "java/lang/reflect/Method.class";

    static {
        Instrumenter.classesToShuffle.add("java/lang/Class.class");
        Instrumenter.classesToShuffle.add("java/lang/reflect/Field.class");
        Instrumenter.classesToShuffle.add("java/io/File.class");
        Instrumenter.classesToShuffle.add("java/text/DateFormatSymbols.class");

        Instrumenter.specialClassesToShuffle.add(Instrumenter.hashMapName);
        Instrumenter.specialClassesToShuffle.add(Instrumenter.concurrentHashMapName);
        Instrumenter.specialClassesToShuffle.add(Instrumenter.methodName);
    }

    private Instrumenter() {
    }

    private interface Function<T, R> {
        R apply(T param);
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int numRead;
        byte[] data = new byte[16384]; // 16KB should be more than enough

        while ((numRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, numRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    private static byte[] toMd5(InputStream is) throws IOException, NoSuchAlgorithmException {
        byte[] toMd5 = Instrumenter.readAllBytes(is);

        MessageDigest md5er = MessageDigest.getInstance("md5");

        return md5er.digest(toMd5);

    }

    // Returns true if the file should be reinstrumented
    private static void writeMd5(InputStream clInputStream, String name, ZipOutputStream zip)
            throws IOException, NoSuchAlgorithmException {
        byte[] md5 = Instrumenter.toMd5(clInputStream);

        ZipEntry zipEntry = new ZipEntry(name);
        zip.putNextEntry(zipEntry);
        zip.write(md5, 0, md5.length);
        zip.closeEntry();
    }


    private static void instrumentClass(String className,
                                        Function<ClassVisitor, ClassVisitor> createShuffler,
                                        ZipFile rt, ZipOutputStream outZip) throws IOException {
        InputStream classStream = null;
        try {
            classStream = rt.getInputStream(rt.getEntry(className));
        } catch (IOException exc) {
            Logger.getGlobal().log(Level.WARNING, "Cannot find " + className + " are you sure this is a valid rt.jar?");
            Logger.getGlobal().log(Level.WARNING, "Continuing without instrumenting: " + className);
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

    private static void filterCachedFrom(HashSet<String> toShuffle, HashSet<String> toCopy,
                                         ZipFile rt, ZipFile outJarZipFile)
            throws IOException, NoSuchAlgorithmException {
        Iterator<String> it = toShuffle.iterator();
        while (it.hasNext()) {
            String cl = it.next();
            InputStream clInputStream = rt.getInputStream(rt.getEntry(cl));
            InputStream md5InputStream = outJarZipFile.getInputStream(outJarZipFile.getEntry(cl + ".md5"));
            if (Arrays.equals(Instrumenter.toMd5(clInputStream), Instrumenter.readAllBytes(md5InputStream))) {
                it.remove();
                toCopy.add(cl);
            }
        }
    }

    private static void filterCached(String outJar, ZipFile rt) throws IOException, NoSuchAlgorithmException {
        if (new File(outJar).exists()) {
            ZipFile outJarZipFile = new ZipFile(outJar);
            Instrumenter.filterCachedFrom(Instrumenter.classesToShuffle, Instrumenter.classesToCopy, rt, outJarZipFile);
            Instrumenter.filterCachedFrom(Instrumenter.specialClassesToShuffle, Instrumenter.specialClassesToCopy,
                    rt, outJarZipFile);
        }
    }

    private static void copyToJar(String cl, ZipFile oldJar, ZipOutputStream outZip) throws IOException {
        InputStream clInputStream = oldJar.getInputStream(oldJar.getEntry(cl));
        byte[] arr = Instrumenter.readAllBytes(clInputStream);

        ZipEntry entry = new ZipEntry(cl);
        outZip.putNextEntry(entry);
        outZip.write(arr, 0, arr.length);
        outZip.closeEntry();
    }

    public static final void instrument(String rtJar, String outJar)
            throws NoSuchAlgorithmException, IOException {
        ZipFile rt = null;
        try {
            rt = new ZipFile(rtJar);
        } catch (IOException exc) {
            Logger.getGlobal().log(Level.SEVERE, "Are you sure you provided a valid path to your rt.jar?");
            throw exc;
        }
        Instrumenter.filterCached(outJar, rt);

        if (Instrumenter.classesToShuffle.isEmpty() && Instrumenter.specialClassesToShuffle.isEmpty()) {
            return;
        }

        ByteArrayOutputStream outZipBaos = new ByteArrayOutputStream();
        ZipOutputStream outZip = new ZipOutputStream(outZipBaos);

        for (String cl : Instrumenter.classesToShuffle) {
            InputStream clInputStream = null;
            try {
                clInputStream = rt.getInputStream(rt.getEntry(cl));
            } catch (IOException exc) {
                Logger.getGlobal().log(Level.SEVERE, "Cannot find " + cl + " are you sure this is a valid rt.jar?");
                throw exc;
            }

            Instrumenter.writeMd5(clInputStream, cl + ".md5", outZip);

            clInputStream = rt.getInputStream(rt.getEntry(cl));

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
        for (String cl : Instrumenter.classesToCopy) {
            ZipFile outZipFile = new ZipFile(outJar);
            Instrumenter.copyToJar(cl, outZipFile, outZip);
        }

        ZipEntry hashIterShuffNodeEntry = new ZipEntry("java/util/HashIteratorShufflerNode.class");
        outZip.putNextEntry(hashIterShuffNodeEntry);
        byte[] hashIterShuffNodeBytes = HashIteratorShufflerNodeASMDump.dump();
        outZip.write(hashIterShuffNodeBytes, 0, hashIterShuffNodeBytes.length);
        outZip.closeEntry();

        ZipEntry hashIterShuffEntryEntry = new ZipEntry("java/util/HashIteratorShufflerEntry.class");
        outZip.putNextEntry(hashIterShuffEntryEntry);
        byte[] hashIterShuffEntryBytes = HashIteratorShufflerEntryASMDump.dump();
        outZip.write(hashIterShuffEntryBytes, 0, hashIterShuffEntryBytes.length);
        outZip.closeEntry();

        if (Instrumenter.specialClassesToShuffle.contains(Instrumenter.hashMapName)) {
            Instrumenter.writeMd5(rt.getInputStream(rt.getEntry(Instrumenter.hashMapName)),
                    Instrumenter.hashMapName + ".md5", outZip);
            Instrumenter.instrumentClass(Instrumenter.hashMapName,
                    new Function<ClassVisitor, ClassVisitor>() {
                        @Override
                        public ClassVisitor apply(ClassVisitor cv) {
                            return new HashMapShufflingAdder(cv);
                        }
                    }, rt, outZip);
        } else {
            Instrumenter.copyToJar(Instrumenter.hashMapName, new ZipFile(outJar), outZip);
        }
        if (Instrumenter.specialClassesToShuffle.contains(Instrumenter.concurrentHashMapName)) {
            Instrumenter.writeMd5(rt.getInputStream(rt.getEntry(Instrumenter.concurrentHashMapName)),
                    Instrumenter.concurrentHashMapName + ".md5", outZip);
            Instrumenter.instrumentClass(Instrumenter.concurrentHashMapName,
                    new Function<ClassVisitor, ClassVisitor>() {
                        @Override
                        public ClassVisitor apply(ClassVisitor cv) {
                            return new ConcurrentHashMapShufflingAdder(cv);
                        }
                    }, rt, outZip);
        } else {
            Instrumenter.copyToJar(Instrumenter.concurrentHashMapName, new ZipFile(outJar), outZip);
        }
        if (Instrumenter.specialClassesToShuffle.contains(Instrumenter.methodName)) {
            Instrumenter.writeMd5(rt.getInputStream(rt.getEntry(Instrumenter.methodName)),
                    Instrumenter.methodName + ".md5", outZip);
            Instrumenter.instrumentClass(Instrumenter.methodName,
                    new Function<ClassVisitor, ClassVisitor>() {
                        @Override
                        public ClassVisitor apply(ClassVisitor cv) {
                            return new MethodShufflingAdder(cv);
                        }
                    }, rt, outZip);
        } else {
            Instrumenter.copyToJar(Instrumenter.methodName, new ZipFile(outJar), outZip);
        }
        outZip.close();

        outZipBaos.writeTo(new FileOutputStream(outJar));
    }
}
