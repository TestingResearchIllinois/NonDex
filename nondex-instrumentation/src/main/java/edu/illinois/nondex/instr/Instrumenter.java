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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
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

    private static byte[] toMd5(InputStream is) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384]; // 16KB should be more than enough

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        byte[] toMd5 = buffer.toByteArray();

        MessageDigest md5er = MessageDigest.getInstance("md5");

        return md5er.digest(toMd5);

    }

    // Returns true if the file should be reinstrumented
    private static boolean writeMd5(InputStream clInputStream, String name, String md5Dir) throws IOException, NoSuchAlgorithmException {
        byte[] md5 = toMd5(clInputStream);
        // TODO make this crossplatform?
        File md5File = new File(md5Dir + "/" + name);
        if (md5File.exists()) {
            byte[] oldMd5 = Files.readAllBytes(Paths.get(md5File.getPath()));

            if (Arrays.equals(md5, oldMd5)) {
                return false;
            }
        }

        FileOutputStream outFile = new FileOutputStream(md5Dir + "/" + name);
        outFile.write(md5);
        return true;
    }


    private static void instrumentClass(String className,
                                        Function<ClassVisitor, ClassVisitor> createShuffler,
                                        ZipFile rt, ZipOutputStream outZip) throws IOException {
        InputStream classStream = rt.getInputStream(rt.getEntry(className));
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

    public static final void instrument(String rtJar, String outJar, String md5Dir) throws IOException, NoSuchAlgorithmException {
        ZipFile rt = new ZipFile(rtJar);
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(outJar));

        for (String cl : classesToShuffle) {
            InputStream clInputStream = rt.getInputStream(rt.getEntry(cl));
            if (writeMd5(clInputStream, cl + ".md5", md5Dir)) {
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
