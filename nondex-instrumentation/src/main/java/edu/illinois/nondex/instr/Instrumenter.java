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

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;


public final class Instrumenter {
    public static final String hashMapName = "java/util/HashMap$HashIterator.class";
    public static final String weakHashMapName = "java/util/WeakHashMap$HashIterator.class";
    public static final String identityHashMapName = "java/util/IdentityHashMap$IdentityHashMapIterator.class";
    public static final String concurrentHashMapName = "java/util/concurrent/ConcurrentHashMap$Traverser.class";
    public static final String methodName = "java/lang/reflect/Method.class";
    public static final String priorityQueueName = "java/util/PriorityQueue$Itr.class";
    public static final String priorityBlockingQueueName = "java/util/concurrent/PriorityBlockingQueue.class";

    private final Set<String> standardClassesToInstrument = new HashSet<>();
    private final Set<String> specialClassesToInstrument = new HashSet<>();

    private static final String rootPath = "modules/java.base";

    private interface Function<T, R> {
        R apply(T param);
    }

    private interface Producer<R> {
        R apply();
    }

    private Instrumenter() {
        this.standardClassesToInstrument.add("java/lang/Class.class");
        this.standardClassesToInstrument.add("java/lang/reflect/Field.class");
        this.standardClassesToInstrument.add("java/io/File.class");
        this.standardClassesToInstrument.add("java/text/DateFormatSymbols.class");
        this.standardClassesToInstrument.add("java/util/PriorityQueue.class");

        this.specialClassesToInstrument.add(Instrumenter.hashMapName);
        this.specialClassesToInstrument.add(Instrumenter.weakHashMapName);
        this.specialClassesToInstrument.add(Instrumenter.identityHashMapName);
        this.specialClassesToInstrument.add(Instrumenter.concurrentHashMapName);
        this.specialClassesToInstrument.add(Instrumenter.methodName);
        this.specialClassesToInstrument.add(Instrumenter.priorityQueueName);
        this.specialClassesToInstrument.add(Instrumenter.priorityBlockingQueueName);
    }

    public static final void instrument(String rtPath, String outJar)
            throws NoSuchAlgorithmException, IOException {
        /* TODO: Current status: read from rt.jar and use out.jar to override
            Verify for JDK9+, can the output gotta still be in .jar format or can be in .jmod?
            (Reference: http://openjdk.java.net/jeps/261#Packaging:-JMOD-files)
        */
        Logger.getGlobal().log(Level.FINE, "Instrumenting " + rtPath + " into " + outJar);
        new Instrumenter().process(rtPath, outJar);
    }

    private void process(String rtPath, String outJar)
            throws IOException, NoSuchAlgorithmException {
        // TODO: To be refactored considering OOP design for JDK9Plus
        if (rtPath.equals(ConfigurationDefaults.JDK9_PLUS_PATH)) {
            processJDK9Plus(outJar);
            return;
        }

        ZipFile rt = null;
        try {
            rt = new ZipFile(rtPath);
        } catch (IOException exc) {
            Logger.getGlobal().log(Level.SEVERE, "Are you sure you provided a valid path to your rt.jar?");
            throw exc;
        }
        final Set<String> classesToCopy = this.filterCached(rt, outJar);

        // If no class needs to be re-instrumented
        if (this.standardClassesToInstrument.isEmpty() && this.specialClassesToInstrument.isEmpty()) {
            return;
        }

        ByteArrayOutputStream outZipBaos = new ByteArrayOutputStream();
        ZipOutputStream outZip = new ZipOutputStream(outZipBaos);

        this.instrumentStandardClasses(rt, outZip);

        if (rt.getEntry("java/util/HashMap$Node.class") != null) {
            this.addAsmDumpResultToZip(outZip, "java/util/HashMap$HashIterator$HashIteratorShuffler.class",
                    new Producer<byte[]>() {
                        @Override
                        public byte[] apply() {
                            return HashIteratorShufflerASMDump.dump("Node");
                        }
                    });
        } else if (rt.getEntry("java/util/HashMap$Entry.class") != null) {
            this.addAsmDumpResultToZip(outZip, "java/util/HashMap$HashIterator$HashIteratorShuffler.class",
                    new Producer<byte[]>() {
                        @Override
                        public byte[] apply() {
                            return HashIteratorShufflerASMDump.dump("Entry");
                        }
                    });
        }

        for (String clz : this.specialClassesToInstrument) {
            this.instrumentSpecialClass(rt, outZip, clz);
        }

        this.copyCachedClassesToOutZip(outJar, classesToCopy, outZip);

        outZip.close();
        FileOutputStream fos = new FileOutputStream(outJar);
        outZipBaos.writeTo(fos);
        fos.close();
    }


    private void processJDK9Plus(String outJar) throws IOException, NoSuchAlgorithmException {
        // FileSystem for jrt -- equivalent for rt.jar (https://openjdk.java.net/jeps/220)
        FileSystem rtFs = FileSystems.getFileSystem(URI.create("jrt:/"));

        ByteArrayOutputStream outZipBaos = new ByteArrayOutputStream();
        ZipOutputStream outZip = new ZipOutputStream(outZipBaos);

        instrumentStandardClassesForJDK9Plus(rtFs, outZip);
        // TODO: omit
        outZip.close();
        FileOutputStream fos = new FileOutputStream(outJar);
        outZipBaos.writeTo(fos);
        fos.close();
        Logger.getGlobal().log(Level.WARNING, "********ProcessJDK9+ OKK!*******");
    }


    private void instrumentStandardClasses(ZipFile rt, ZipOutputStream outZip)
            throws IOException, NoSuchAlgorithmException {
        for (String cl : this.standardClassesToInstrument) {
            InputStream clInputStream = null;
            try {
                ZipEntry entry = rt.getEntry(cl);
                if (entry == null) {
                    Logger.getGlobal().log(Level.WARNING, "Could not find " + cl + " in rt.jar");
                    Logger.getGlobal().log(Level.WARNING, "Are you running java 8?");
                    Logger.getGlobal().log(Level.WARNING, "Continuing without instrumenting: " + cl);
                    continue;
                }
                clInputStream = rt.getInputStream(entry);
            } catch (IOException exc) {
                // I am not sure this code is reachable
                // TODO(gyori): Test that this code is reachable;
                Logger.getGlobal().log(Level.WARNING, "Cannot find " + cl + " are you sure this is a valid rt.jar?");
                Logger.getGlobal().log(Level.WARNING, "Continuing without insturmenting: " + cl);
                continue;
            }

            this.writeMd5(clInputStream, cl + ".md5", outZip);

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
    }

    private void instrumentStandardClassesForJDK9Plus(FileSystem rtFS, ZipOutputStream outZip) throws IOException, NoSuchAlgorithmException {
        for (String cls: this.standardClassesToInstrument) {
            InputStream clInputStream = null;

            Path curClsPath = rtFS.getPath(rootPath, cls);
            byte[] clsBytes = Files.readAllBytes(curClsPath);
            clInputStream = new ByteArrayInputStream(clsBytes);
            this.writeMd5(clInputStream, cls + ".md5", outZip);

            clInputStream = new ByteArrayInputStream(clsBytes);

            ClassReader cr = new ClassReader(clInputStream);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            ClassVisitor cv = new ClassVisitorShufflingAdder(cw);

            cr.accept(cv, 0);

            byte[] arr = cw.toByteArray();

            ZipEntry entry = new ZipEntry(cls);
            outZip.putNextEntry(entry);
            outZip.write(arr, 0, arr.length);
            outZip.closeEntry();

            Logger.getGlobal().log(Level.WARNING, "********Process class" + cls + "OK!*******");
        }
    }


    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int numRead;
        byte[] data = new byte[4096]; // 4KB should be more than enough

        while ((numRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, numRead);
        }

        return buffer.toByteArray();
    }

    private byte[] toMd5(InputStream is) throws IOException, NoSuchAlgorithmException {
        byte[] toMd5 = this.readAllBytes(is);
        MessageDigest md5er = MessageDigest.getInstance("md5");
        return md5er.digest(toMd5);
    }

    private void writeMd5(InputStream isToComputeHashOn, String fileName, ZipOutputStream zip)
            throws IOException, NoSuchAlgorithmException {
        if (isToComputeHashOn == null) {
            Logger.getGlobal().log(Level.WARNING, "Could not find " + fileName + " in rt.jar");
            Logger.getGlobal().log(Level.WARNING, "Are you running java 8?");
            Logger.getGlobal().log(Level.WARNING, "Continuing without instrumenting: " + fileName);
            return;
        }
        byte[] md5 = this.toMd5(isToComputeHashOn);

        ZipEntry zipEntry = new ZipEntry(fileName);
        zip.putNextEntry(zipEntry);
        zip.write(md5, 0, md5.length);
        zip.closeEntry();
    }


    private void instrumentClass(String className,
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
        ClassVisitor cv = createShuffler.apply(cw);

        cr.accept(cv, 0);
        byte[] arr = cw.toByteArray();

        cr = new ClassReader(arr);
        cr.accept(new CheckClassAdapter(new ClassWriter(0)), 0);

        ZipEntry zipEntry = new ZipEntry(className);
        outZip.putNextEntry(zipEntry);
        outZip.write(arr, 0, arr.length);
        outZip.closeEntry();
    }

    private Set<String> removeCachedFromShufflingList(Set<String> toShuffle, ZipFile rt, ZipFile outJarZipFile)
            throws IOException, NoSuchAlgorithmException {
        Set<String> toCopy = new HashSet<String>();
        Iterator<String> it = toShuffle.iterator();
        while (it.hasNext()) {
            String cl = it.next();

            ZipEntry entry = rt.getEntry(cl);
            if (entry == null) {
                continue;
            }
            InputStream clInputStream = rt.getInputStream(entry);

            entry = outJarZipFile.getEntry(cl + ".md5");
            if (entry == null) {
                continue;
            }
            InputStream md5InputStream = outJarZipFile.getInputStream(entry);

            if (Arrays.equals(this.toMd5(clInputStream), this.readAllBytes(md5InputStream))) {
                it.remove();
                toCopy.add(cl);
            }
        }
        return toCopy;
    }

    /*
        Get a set of classes to be instrumented if out.jar exists
    */
    private Set<String> filterCached(ZipFile rt, String oldJar) throws IOException, NoSuchAlgorithmException {
        Set<String> classesToCopy = new HashSet<>();
        if (new File(oldJar).exists()) {
            ZipFile outJarZipFile = new ZipFile(oldJar);
            classesToCopy.addAll(this.removeCachedFromShufflingList(this.standardClassesToInstrument,
                    rt, outJarZipFile));
            classesToCopy.addAll(this.removeCachedFromShufflingList(this.specialClassesToInstrument,
                    rt, outJarZipFile));
        }
        return classesToCopy;
    }

    private <T extends CVFactory> void instrumentSpecialClass(final ZipFile rt, ZipOutputStream outZip, final String clz)
            throws IOException, NoSuchAlgorithmException {
        ZipEntry entry = rt.getEntry(clz);
        if (entry == null) {
            Logger.getGlobal().log(Level.WARNING, "Could not find " + clz + " in rt.jar");
            Logger.getGlobal().log(Level.WARNING, "Are you sure you're running java 8?");
            Logger.getGlobal().log(Level.WARNING, "Continuing without instrumenting: " + clz);
            return;
        }
        this.writeMd5(rt.getInputStream(rt.getEntry(clz)), clz + ".md5", outZip);
        this.instrumentClass(clz,
                new Function<ClassVisitor, ClassVisitor>() {
                    @Override
                    public ClassVisitor apply(ClassVisitor cv) {
                        try {
                            return CVFactory.construct(cv, clz, rt);
                        } catch (NoSuchAlgorithmException nsaException) {
                            return null;
                        }
                    }
                }, rt, outZip);
    }

    private void addAsmDumpResultToZip(ZipOutputStream outZip, String entryName, Producer<byte[]> classProducer)
            throws IOException {
        ZipEntry hashIterShuffNodeEntry = new ZipEntry(entryName);
        outZip.putNextEntry(hashIterShuffNodeEntry);
        byte[] hashIterShuffNodeBytes = classProducer.apply();
        outZip.write(hashIterShuffNodeBytes, 0, hashIterShuffNodeBytes.length);
        outZip.closeEntry();
    }

    private void copyCachedClassesToOutZip(String oldJar, final Set<String> classesToCopy, ZipOutputStream outZip)
            throws IOException {
        if (new File(oldJar).exists()) {
            ZipFile outZipFile = new ZipFile(oldJar);

            for (String cl : classesToCopy) {
                InputStream clInputStream = outZipFile.getInputStream(outZipFile.getEntry(cl));
                byte[] arr = this.readAllBytes(clInputStream);

                ZipEntry entry = new ZipEntry(cl);
                outZip.putNextEntry(entry);
                outZip.write(arr, 0, arr.length);
                outZip.closeEntry();
            }

            outZipFile.close();
        }
    }

    /*
        Get all entries to the bottom-level class file
    */
    private void getAllEntries() {
//        ArrayList<Path> paths = new ArrayList<>();
//        Queue<Path> pathQueue = new LinkedList<>();
//        Files.walk(rootPath).forEach(pathQueue::add);
//
//        while (!pathQueue.isEmpty()) {
//            Path curDir = pathQueue.peek();
//            Logger.getGlobal().log(Level.WARNING, "******Directory:" + curDir.toString());
//            if (Files.isDirectory(curDir)) {
//                Files.walk(curDir).forEach(pathQueue::add);
//            } else {
//                paths.add(curDir);
//            }
//            pathQueue.poll();
//        }
    }
}
