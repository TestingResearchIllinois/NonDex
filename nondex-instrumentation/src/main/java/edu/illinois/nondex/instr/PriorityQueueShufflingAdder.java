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

public class PriorityQueueShufflingAdder extends ClassVisitor {

    public PriorityQueueShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM9, ca);
    }

    public void addElements() {
        FieldVisitor fv = super.visitField(Opcodes.ACC_PRIVATE, "elements", "Ljava/util/List;", "Ljava/util/List<TE;>;",
                null);
        fv.visitEnd();
    }

    public void addIter() {
        FieldVisitor fv = super.visitField(Opcodes.ACC_PRIVATE, "iter", "Ljava/util/Iterator;",
                "Ljava/util/Iterator<TE;>;", null);
        fv.visitEnd();
    }

    public void addNext() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "next", "()Ljava/lang/Object;", "()TE;", null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "expectedModCount", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "this$0", "Ljava/util/PriorityQueue;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue", "modCount", "I");
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
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "iter", "Ljava/util/Iterator;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRetElt", "Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ICONST_M1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRet", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "lastRetElt", "Ljava/lang/Object;");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    public void addHasNext() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "hasNext", "()Z", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "iter", "Ljava/util/Iterator;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        addElements();
        addIter();
        addNext();
        addHasNext();
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("hasNext".equals(name)) {
            return super.visitMethod(access, "originalHasNext", desc, signature, exceptions);
        }
        if ("next".equals(name)) {
            return super.visitMethod(access, "originalNext", desc, signature, exceptions);
        }
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 1);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "this$0",
                                "Ljava/util/PriorityQueue;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ICONST_0);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "cursor", "I");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ICONST_M1);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRet", "I");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "forgetMeNot",
                                "Ljava/util/ArrayDeque;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRetElt",
                                "Ljava/lang/Object;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "this$0",
                                "Ljava/util/PriorityQueue;");
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue", "modCount", "I");
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "expectedModCount", "I");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "elements",
                                "Ljava/util/List;");
                        Label l0 = new Label();
                        super.visitLabel(l0);
                        super.visitFrame(Opcodes.F_FULL, 2,
                                new Object[] { "java/util/PriorityQueue$Itr", "java/util/PriorityQueue" }, 0,
                                new Object[] {});
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/PriorityQueue$Itr", "originalHasNext",
                                "()Z", false);
                        Label l1 = new Label();
                        super.visitJumpInsn(Opcodes.IFEQ, l1);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "elements",
                                "Ljava/util/List;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/PriorityQueue$Itr", "originalNext",
                                "()Ljava/lang/Object;", false);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z",
                                true);
                        super.visitInsn(Opcodes.POP);
                        super.visitJumpInsn(Opcodes.GOTO, l0);
                        super.visitLabel(l1);
                        super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "elements",
                                "Ljava/util/List;");
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                                "(Ljava/util/List;)Ljava/util/List;", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "elements",
                                "Ljava/util/List;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "elements",
                                "Ljava/util/List;");
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator",
                                "()Ljava/util/Iterator;", true);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "iter",
                                "Ljava/util/Iterator;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ICONST_M1);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRet", "I");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRetElt",
                                "Ljava/lang/Object;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
