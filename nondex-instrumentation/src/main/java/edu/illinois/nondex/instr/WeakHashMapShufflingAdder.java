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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WeakHashMapShufflingAdder extends ClassVisitor {

    public WeakHashMapShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);
    }

    public void addIter() {
        FieldVisitor fv = super.visitField(Opcodes.ACC_PRIVATE, "iter", "Ljava/util/Iterator;",
                "Ljava/util/Iterator<Ljava/util/WeakHashMap$Entry<TK;TV;>;>;", null);
        fv.visitEnd();
    }

    public void addInit() {
        MethodVisitor mv = super.visitMethod(0, "<init>", "(Ljava/util/WeakHashMap;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "this$0", "Ljava/util/WeakHashMap;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "entry",
                "Ljava/util/WeakHashMap$Entry;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "lastReturned",
                "Ljava/util/WeakHashMap$Entry;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap$HashIterator", "this$0", "Ljava/util/WeakHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap", "modCount", "I");
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "expectedModCount", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "nextKey", "Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "currentKey", "Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "iter", "Ljava/util/Iterator;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/WeakHashMap", "isEmpty", "()Z", false);
        Label l0 = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, l0);
        mv.visitInsn(Opcodes.ICONST_0);
        Label l1 = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_FULL, 2, new Object[] { "java/util/WeakHashMap$HashIterator", "java/util/WeakHashMap" },
                1, new Object[] { "java/util/WeakHashMap$HashIterator" });
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap", "table", "[Ljava/util/WeakHashMap$Entry;");
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_FULL, 2, new Object[] { "java/util/WeakHashMap$HashIterator", "java/util/WeakHashMap" },
                2, new Object[] { "java/util/WeakHashMap$HashIterator", Opcodes.INTEGER });
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "index", "I");
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, 2);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/List" }, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/WeakHashMap$HashIterator", "originalHasNext", "()Z",
                false);
        Label l3 = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, l3);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/WeakHashMap$HashIterator", "originalNextEntry",
                "()Ljava/util/WeakHashMap$Entry;", false);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(Opcodes.POP);
        mv.visitJumpInsn(Opcodes.GOTO, l2);
        mv.visitLabel(l3);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                "(Ljava/util/List;)Ljava/util/List;", false);
        mv.visitVarInsn(Opcodes.ASTORE, 2);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "iter", "Ljava/util/Iterator;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "lastReturned",
                "Ljava/util/WeakHashMap$Entry;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();
    }

    public void addHasNext() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "hasNext", "()Z", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap$HashIterator", "iter", "Ljava/util/Iterator;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    public void addNextEntry() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "nextEntry", "()Ljava/util/WeakHashMap$Entry;",
                "()Ljava/util/WeakHashMap$Entry<TK;TV;>;", null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap$HashIterator", "this$0", "Ljava/util/WeakHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap", "modCount", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap$HashIterator", "expectedModCount", "I");
        Label l0 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ICMPEQ, l0);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ConcurrentModificationException");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ConcurrentModificationException", "<init>", "()V", false);
        mv.visitInsn(Opcodes.ATHROW);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap$HashIterator", "iter", "Ljava/util/Iterator;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/util/WeakHashMap$Entry");
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "lastReturned",
                "Ljava/util/WeakHashMap$Entry;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap$HashIterator", "lastReturned",
                "Ljava/util/WeakHashMap$Entry;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/WeakHashMap$Entry", "get", "()Ljava/lang/Object;", false);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "currentKey", "Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/WeakHashMap$HashIterator", "lastReturned",
                "Ljava/util/WeakHashMap$Entry;");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        addIter();
        addInit();
        addHasNext();
        addNextEntry();
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if ("hasNext".equals(name)) {
            MethodVisitor original = super.visitMethod(access, "originalHasNext", desc, signature, exceptions);
            return original;
        }
        if ("nextEntry".equals(name)) {
            MethodVisitor original = super.visitMethod(access, "originalNextEntry", desc, signature, exceptions);
            return original;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
