/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2022 Kaiyao Ke
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
        super(Opcodes.ASM9, ca);
    }

    public void addIter() {
        FieldVisitor fv = super.visitField(Opcodes.ACC_PRIVATE, "iter", "Ljava/util/Iterator;",
                "Ljava/util/Iterator<Ljava/util/WeakHashMap$Entry<TK;TV;>;>;", null);
        fv.visitEnd();
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
        addHasNext();
        addNextEntry();
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("hasNext".equals(name)) {
            return super.visitMethod(access, "originalHasNext", desc, signature, exceptions);
        }
        if ("nextEntry".equals(name)) {
            return super.visitMethod(access, "originalNextEntry", desc, signature, exceptions);
        }
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        // Create ArrayList to collect entries
                        super.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
                        super.visitVarInsn(Opcodes.ASTORE, 2);

                        // Populate list with entries using originalHasNext/originalNextEntry
                        Label loopStart = new Label();
                        super.visitLabel(loopStart);
                        super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/List" }, 0, null);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/WeakHashMap$HashIterator",
                                "originalHasNext", "()Z", false);
                        Label loopEnd = new Label();
                        super.visitJumpInsn(Opcodes.IFEQ, loopEnd);
                        super.visitVarInsn(Opcodes.ALOAD, 2);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/WeakHashMap$HashIterator",
                                "originalNextEntry", "()Ljava/util/WeakHashMap$Entry;", false);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z",
                                true);
                        super.visitInsn(Opcodes.POP);
                        super.visitJumpInsn(Opcodes.GOTO, loopStart);

                        super.visitLabel(loopEnd);
                        super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                        // Shuffle the list
                        super.visitVarInsn(Opcodes.ALOAD, 2);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                                "(Ljava/util/List;)Ljava/util/List;", false);
                        super.visitVarInsn(Opcodes.ASTORE, 2);

                        // Set iter field to the shuffled list's iterator
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 2);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator",
                                "()Ljava/util/Iterator;", true);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "iter",
                                "Ljava/util/Iterator;");

                        // Reset lastReturned to null
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/WeakHashMap$HashIterator", "lastReturned",
                                "Ljava/util/WeakHashMap$Entry;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
