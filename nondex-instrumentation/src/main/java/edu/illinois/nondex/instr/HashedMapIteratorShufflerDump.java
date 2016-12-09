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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HashedMapIteratorShufflerDump implements Opcodes {

    public static byte[] dump() {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER,
            "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "<K:Ljava/lang/Object;V:Ljava/lang/Object;>Ljava/lang/Object;", "java/lang/Object", null);
        cw.visitInnerClass("org/apache/commons/collections4/map/AbstractHashedMap$HashEntry",
            "org/apache/commons/collections4/map/AbstractHashedMap", "HashEntry", ACC_PROTECTED + ACC_STATIC);
        cw.visitInnerClass(
            "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
            "org/apache/commons/collections4/map/AbstractHashedMap", "HashIterator",
            ACC_PROTECTED + ACC_STATIC + ACC_ABSTRACT);
        cw.visitInnerClass(
            "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "org/apache/commons/collections4/map/AbstractHashedMap", "HashedMapIteratorShuffler",
            ACC_PROTECTED + ACC_STATIC);

        fv = cw.visitField(ACC_PRIVATE, "iter", "Ljava/util/Iterator;",
            "Ljava/util/Iterator<Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry<TK;TV;>;>;", null);
        fv.visitEnd();


        fv = cw.visitField(ACC_PRIVATE, "hashIter",
            "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;", null, null);
        fv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
            "(Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object",
            "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "hashIter", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;");
        mv.visitTypeInsn(NEW, "java/util/ArrayList");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 2);
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_FULL, 3, new Object[] {
            "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator", "java/util/List"}, 0, new Object[] {});
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "hashIter", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
            "original_hasNext", "()Z", false);
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "hashIter", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
            "original_nextEntry", "()Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Collections", "shuffle", "(Ljava/util/List;)V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "hashIter", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;");
        mv.visitInsn(ACONST_NULL);
        mv.visitFieldInsn(PUTFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
            "last", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
        mv.visitFieldInsn(PUTFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
             "iter", "Ljava/util/Iterator;");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "nextEntry",
            "(I)Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;",
            "(I)Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry<TK;TV;>;", null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "hashIter", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;");
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
            "expectedModCount", "I");
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l2);
        mv.visitTypeInsn(NEW, "java/util/ConcurrentModificationException");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ConcurrentModificationException",
            "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "hashIter", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "iter", "Ljava/util/Iterator;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(CHECKCAST, "org/apache/commons/collections4/map/AbstractHashedMap$HashEntry");
        mv.visitFieldInsn(PUTFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
            "last", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "hashIter", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;");
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
            "last", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "hasNext", "()Z", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
            "iter", "Ljava/util/Iterator;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        cw.visitEnd();

        return cw.toByteArray();
    }
}
