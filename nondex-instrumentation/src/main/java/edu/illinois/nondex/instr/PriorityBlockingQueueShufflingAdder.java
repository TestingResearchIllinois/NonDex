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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class PriorityBlockingQueueShufflingAdder extends ClassVisitor {

    public PriorityBlockingQueueShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM9, ca);
    }

    public void addToString() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, null);
        Label l3 = new Label();
        Label l4 = new Label();
        mv.visitTryCatchBlock(l3, l4, l2, null);
        Label l5 = new Label();
        mv.visitTryCatchBlock(l2, l5, l2, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/concurrent/PriorityBlockingQueue", "lock",
                "Ljava/util/concurrent/locks/ReentrantLock;");
        mv.visitVarInsn(Opcodes.ASTORE, 1);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/locks/ReentrantLock", "lock", "()V", false);
        mv.visitLabel(l0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/concurrent/PriorityBlockingQueue", "size", "I");
        mv.visitVarInsn(Opcodes.ISTORE, 2);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitJumpInsn(Opcodes.IFNE, l3);
        mv.visitLdcInsn("[]");
        mv.visitVarInsn(Opcodes.ASTORE, 3);
        mv.visitLabel(l1);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/locks/ReentrantLock", "unlock", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitLabel(l3);
        mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] { "java/util/concurrent/locks/ReentrantLock", Opcodes.INTEGER },
                0, null);
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, 3);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitIntInsn(Opcodes.BIPUSH, 91);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                false);
        mv.visitInsn(Opcodes.POP);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/concurrent/PriorityBlockingQueue", "queue",
                "[Ljava/lang/Object;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "[Ljava/lang/Object;", "clone", "()Ljava/lang/Object;", false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "[Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ASTORE, 4);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                "([Ljava/lang/Object;)[Ljava/lang/Object;", false);
        mv.visitVarInsn(Opcodes.ASTORE, 4);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ISTORE, 5);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitFrame(Opcodes.F_APPEND, 3,
                new Object[] { "java/lang/StringBuilder", "[Ljava/lang/Object;", Opcodes.INTEGER }, 0, null);
        mv.visitVarInsn(Opcodes.ILOAD, 5);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        Label l7 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ICMPGE, l7);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitVarInsn(Opcodes.ILOAD, 5);
        mv.visitInsn(Opcodes.AALOAD);
        mv.visitVarInsn(Opcodes.ASTORE, 6);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitVarInsn(Opcodes.ALOAD, 6);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        Label l8 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ACMPNE, l8);
        mv.visitLdcInsn("(this Collection)");
        Label l9 = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, l9);
        mv.visitLabel(l8);
        mv.visitFrame(Opcodes.F_FULL, 7,
                new Object[] { "java/util/concurrent/PriorityBlockingQueue", "java/util/concurrent/locks/ReentrantLock",
                    Opcodes.INTEGER, "java/lang/StringBuilder", "[Ljava/lang/Object;", Opcodes.INTEGER,
                    "java/lang/Object" },
                1, new Object[] { "java/lang/StringBuilder" });
        mv.visitVarInsn(Opcodes.ALOAD, 6);
        mv.visitLabel(l9);
        mv.visitFrame(Opcodes.F_FULL, 7,
                new Object[] { "java/util/concurrent/PriorityBlockingQueue", "java/util/concurrent/locks/ReentrantLock",
                    Opcodes.INTEGER, "java/lang/StringBuilder", "[Ljava/lang/Object;", Opcodes.INTEGER,
                    "java/lang/Object" },
                2, new Object[] { "java/lang/StringBuilder", "java/lang/Object" });
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitVarInsn(Opcodes.ILOAD, 5);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.ISUB);
        Label l10 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ICMPEQ, l10);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitIntInsn(Opcodes.BIPUSH, 44);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                false);
        mv.visitIntInsn(Opcodes.BIPUSH, 32);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                false);
        mv.visitInsn(Opcodes.POP);
        mv.visitLabel(l10);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        mv.visitIincInsn(5, 1);
        mv.visitJumpInsn(Opcodes.GOTO, l6);
        mv.visitLabel(l7);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitIntInsn(Opcodes.BIPUSH, 93);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitVarInsn(Opcodes.ASTORE, 5);
        mv.visitLabel(l4);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/locks/ReentrantLock", "unlock", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 5);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_FULL, 2, new Object[] { "java/util/concurrent/PriorityBlockingQueue",
            "java/util/concurrent/locks/ReentrantLock" }, 1, new Object[] { "java/lang/Throwable" });
        mv.visitVarInsn(Opcodes.ASTORE, 7);
        mv.visitLabel(l5);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/locks/ReentrantLock", "unlock", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 7);
        mv.visitInsn(Opcodes.ATHROW);
        mv.visitMaxs(3, 8);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        addToString();
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("toString".equals(name)) {
            return super.visitMethod(access, "originalToString", desc, signature, exceptions);
        }
        if ("toArray".equals(name)) {
            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ARETURN) {
                        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                                "([Ljava/lang/Object;)[Ljava/lang/Object;", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
