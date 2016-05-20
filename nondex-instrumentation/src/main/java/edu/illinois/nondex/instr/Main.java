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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;


public class Main {
    public static final boolean DEBUG = false;

    private static final HashSet<String> classesToShuffle = new HashSet<>();


    static {
        classesToShuffle.add("java/lang/Class.class");
        classesToShuffle.add("java/lang/reflect/Field.class");
        classesToShuffle.add("java/io/File.class");
    }

    public static void main(String...args) throws Exception {
        ZipFile rt = new ZipFile(args[0]);
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream("out.jar"));

        for (String cl : classesToShuffle) {
            InputStream clInputStream = rt.getInputStream(rt.getEntry(cl));

            ClassReader cr = new ClassReader(clInputStream);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            ClassVisitor cv = new AddShufflingToClassVisitor(cw);

            cr.accept(cv, 0);

            byte[] arr = cw.toByteArray();

            ZipEntry entry = new ZipEntry(cl);
            outZip.putNextEntry(entry);
            outZip.write(arr, 0, arr.length);
            outZip.closeEntry();
        }

        InputStream hashmapStream = rt.getInputStream(rt.getEntry("java/util/HashMap$HashIterator.class"));
        ClassReader cr = new ClassReader(hashmapStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        CheckClassAdapter ca = new CheckClassAdapter(cw);
        ClassVisitor cv = new AddShufflingToHashMap(ca);

        cr.accept(cv, 0);
        byte[] arr = cw.toByteArray();

        ZipEntry entry = new ZipEntry("java/util/HashMap$HashIterator.class");
        outZip.putNextEntry(entry);
        outZip.write(arr, 0, arr.length);
        outZip.closeEntry();

        outZip.close();
    }
}
