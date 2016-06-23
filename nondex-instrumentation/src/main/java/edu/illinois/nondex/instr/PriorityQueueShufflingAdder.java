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

    private MethodProperties hasNextProp;
    private MethodProperties nextProp;

    private class MethodProperties {
        private int access;
        private String name;
        private String desc;
        private String signature;
        private String[] exceptions;
    }

    public PriorityQueueShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);

        hasNextProp = new MethodProperties();
        nextProp = new MethodProperties();
    }

    public void addElements() {
        FieldVisitor fv = super.visitField(0, "elements", "Ljava/util/List;", "Ljava/util/List<TE;>;", null);
        fv.visitEnd();
    }

    public void addIndex() {
        FieldVisitor fv = super.visitField(0, "index", "I", null, null);
        fv.visitEnd();
    }

    public void addNext() {
        /*MethodVisitor mv = super.visitMethod(nextProp.access, nextProp.name,
                nextProp.desc, nextProp.signature, nextProp.exceptions);*/
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "next", "()Ljava/lang/Object;", "()TE;", null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "elements", "Ljava/util/List;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.DUP);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "index", "I");
        mv.visitInsn(Opcodes.DUP_X1);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.IADD);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "index", "I");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(5, 1);
        mv.visitEnd();
    }

    public void addHasNext() {
        /*MethodVisitor mv = super.visitMethod(hasNextProp.access, hasNextProp.name,
                hasNextProp.desc, hasNextProp.signature, hasNextProp.exceptions);*/
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "hasNext", "()Z", null, null);

        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "index", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "elements", "Ljava/util/List;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I", true);
        Label l0 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ICMPGE, l0);
        mv.visitInsn(Opcodes.ICONST_1);
        Label l1 = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    public void addInit() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/util/PriorityQueue;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "this$0", "Ljava/util/PriorityQueue;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "cursor", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ICONST_M1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRet", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "forgetMeNot", "Ljava/util/ArrayDeque;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "lastRetElt", "Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue$Itr", "this$0", "Ljava/util/PriorityQueue;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/PriorityQueue", "modCount", "I");
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "expectedModCount", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "index", "I");
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, 2);
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_FULL, 3, new Object[]
            {"java/util/PriorityQueue$Itr", "java/util/PriorityQueue",
             "java/util/List"}, 0, new Object[]{});
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/PriorityQueue$Itr", "hasNextOrig", "()Z", false);
        Label l1 = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, l1);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/PriorityQueue$Itr", "nextOrig",
                "()Ljava/lang/Object;", false);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(Opcodes.POP);
        mv.visitJumpInsn(Opcodes.GOTO, l0);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                "(Ljava/util/List;)Ljava/util/List;", false);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/PriorityQueue$Itr", "elements", "Ljava/util/List;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        addInit();
        addNext();
        addHasNext();
        addElements();
        addIndex();

        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if ("hasNext".equals(name)) {
            hasNextProp.access = access;
            hasNextProp.name = name;
            hasNextProp.desc = desc;
            hasNextProp.signature = signature;
            hasNextProp.exceptions = exceptions;

            MethodVisitor original = super.visitMethod(access, "hasNextOrig", desc, signature, exceptions);

            return original;
        }
        if ("next".equals(name)) {
            nextProp.access = access;
            nextProp.name = name;
            nextProp.desc = desc;
            nextProp.signature = signature;
            nextProp.exceptions = exceptions;

            MethodVisitor original = super.visitMethod(access, "nextOrig", desc, signature, exceptions);

            return original;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
