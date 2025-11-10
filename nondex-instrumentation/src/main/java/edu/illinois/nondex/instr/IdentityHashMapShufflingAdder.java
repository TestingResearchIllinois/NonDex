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

public class IdentityHashMapShufflingAdder extends ClassVisitor {

    public IdentityHashMapShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM9, ca);
    }

    public void addOrder() {
        FieldVisitor fv = super.visitField(0, "order", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/Integer;>;",
                null);
        fv.visitEnd();
    }

    public void addKeys() {
        FieldVisitor fv = super.visitField(0, "keys", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/Object;>;", null);
        fv.visitEnd();
    }

    public void addIdx() {
        FieldVisitor fv = super.visitField(0, "idx", "I", null, null);
        fv.visitEnd();
    }

    public void addNextIndex() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PROTECTED, "nextIndex", "()I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "this$0",
                "Ljava/util/IdentityHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "modCount", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "expectedModCount",
                "I");
        Label l0 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ICMPEQ, l0);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ConcurrentModificationException");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ConcurrentModificationException", "<init>", "()V", false);
        mv.visitInsn(Opcodes.ATHROW);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/IdentityHashMap$IdentityHashMapIterator", "hasNext", "()Z",
                false);
        Label l1 = new Label();
        mv.visitJumpInsn(Opcodes.IFNE, l1);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/NoSuchElementException");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/NoSuchElementException", "<init>", "()V", false);
        mv.visitInsn(Opcodes.ATHROW);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "keys",
                "Ljava/util/List;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.DUP);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "idx", "I");
        mv.visitInsn(Opcodes.DUP_X1);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.IADD);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "idx", "I");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
        mv.visitVarInsn(Opcodes.ASTORE, 1);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ISTORE, 2);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] { "java/lang/Object", Opcodes.INTEGER }, 0, null);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "this$0",
                "Ljava/util/IdentityHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "table", "[Ljava/lang/Object;");
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        Label l3 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ICMPGE, l3);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "this$0",
                "Ljava/util/IdentityHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "table", "[Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitInsn(Opcodes.AALOAD);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        Label l4 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ACMPNE, l4);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "lastReturnedIndex",
                "I");
        mv.visitJumpInsn(Opcodes.GOTO, l3);
        mv.visitLabel(l4);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitIincInsn(2, 2);
        mv.visitJumpInsn(Opcodes.GOTO, l2);
        mv.visitLabel(l3);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "lastReturnedIndex",
                "I");
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(5, 3);
        mv.visitEnd();
    }

    public void addHasNext() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "hasNext", "()Z", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "idx", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "order",
                "Ljava/util/List;");
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
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { Opcodes.INTEGER });
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        addOrder();
        addKeys();
        addIdx();
        addNextIndex();
        addHasNext();
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("hasNext".equals(name)) {
            return super.visitMethod(access, "originalHasNext", desc, signature, exceptions);
        }
        if ("nextIndex".equals(name)) {
            return super.visitMethod(access, "originalNextIndex", desc, signature, exceptions);
        }
        if ("<init>".equals(name) && "(Ljava/util/IdentityHashMap;)V".equals(desc)) {
            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        // Initialize order field with new ArrayList
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "order", "Ljava/util/List;");

                        // Initialize keys field with new ArrayList
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "keys", "Ljava/util/List;");

                        // Initialize idx to 0
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitInsn(Opcodes.ICONST_0);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "idx", "I");

                        // Populate the order list by calling originalHasNext/originalNextIndex
                        Label loopStart = new Label();
                        super.visitLabel(loopStart);
                        super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                "java/util/IdentityHashMap$IdentityHashMapIterator", "originalHasNext", "()Z", false);
                        Label loopEnd = new Label();
                        super.visitJumpInsn(Opcodes.IFEQ, loopEnd);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "order", "Ljava/util/List;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                "java/util/IdentityHashMap$IdentityHashMapIterator", "originalNextIndex", "()I", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf",
                                "(I)Ljava/lang/Integer;", false);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z",
                                true);
                        super.visitInsn(Opcodes.POP);
                        super.visitJumpInsn(Opcodes.GOTO, loopStart);

                        super.visitLabel(loopEnd);
                        super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                        // Shuffle the order list
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "order", "Ljava/util/List;");
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                                "(Ljava/util/List;)Ljava/util/List;", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "order", "Ljava/util/List;");

                        // Populate keys list from shuffled order
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "order", "Ljava/util/List;");
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator",
                                "()Ljava/util/Iterator;", true);
                        super.visitVarInsn(Opcodes.ASTORE, 2);

                        Label iterStart = new Label();
                        super.visitLabel(iterStart);
                        super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/Iterator" }, 0, null);
                        super.visitVarInsn(Opcodes.ALOAD, 2);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                        Label iterEnd = new Label();
                        super.visitJumpInsn(Opcodes.IFEQ, iterEnd);
                        super.visitVarInsn(Opcodes.ALOAD, 2);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next",
                                "()Ljava/lang/Object;", true);
                        super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
                        super.visitVarInsn(Opcodes.ASTORE, 3);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "keys", "Ljava/util/List;");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                                "this$0", "Ljava/util/IdentityHashMap;");
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "table",
                                "[Ljava/lang/Object;");
                        super.visitVarInsn(Opcodes.ALOAD, 3);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                        super.visitInsn(Opcodes.AALOAD);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z",
                                true);
                        super.visitInsn(Opcodes.POP);
                        super.visitJumpInsn(Opcodes.GOTO, iterStart);
                        super.visitLabel(iterEnd);
                        super.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}