package edu.illinois.nondex.instr;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HashIteratorShufflerASMDump implements Opcodes {

    public static byte[] dump(String type) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(52, ACC_SUPER, "java/util/HashMap$HashIterator$HashIteratorShuffler", null, "java/lang/Object", null);
        cw.visitInnerClass("java/util/HashMap$" + type, "java/util/HashMap", type, ACC_STATIC);
        cw.visitInnerClass("java/util/HashMap$HashIterator", "java/util/HashMap", "HashIterator", ACC_ABSTRACT);
        cw.visitInnerClass("java/util/HashMap$HashIterator$HashIteratorShuffler", "java/util/HashMap$HashIterator",
                "HashIteratorShuffler", 0);

        {
            fv = cw.visitField(ACC_PRIVATE, "iter", "Ljava/util/Iterator;",
                    "Ljava/util/Iterator<Ljava/util/HashMap$" + type + "<TK;TV;>;>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "hashIter", "Ljava/util/HashMap$HashIterator;",
                    "Ljava/util/HashMap<TK;TV;>.HashIterator;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$1", "Ljava/util/HashMap$HashIterator;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                    "(Ljava/util/HashMap$HashIterator;Ljava/util/HashMap$HashIterator;)V",
                    "(Ljava/util/HashMap<TK;TV;>.HashIterator;)V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "this$1",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(PUTFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hashIter",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitTypeInsn(NEW, "java/util/ArrayList");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(F_FULL, 4,
                    new Object[] { "java/util/HashMap$HashIterator$HashIteratorShuffler",
                            "java/util/HashMap$HashIterator", "java/util/HashMap$HashIterator", "java/util/List" },
                    0, new Object[] {});
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hashIter",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap$HashIterator", "original_hasNext", "()Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hashIter",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap$HashIterator", "original_next" + type,
                    "()Ljava/util/HashMap$" + type + ";", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(POP);
            mv.visitJumpInsn(GOTO, l0);
            mv.visitLabel(l1);
            mv.visitFrame(F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                    "(Ljava/util/List;)Ljava/util/List;", false);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hashIter",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitInsn(ACONST_NULL);
            mv.visitFieldInsn(PUTFIELD, "java/util/HashMap$HashIterator", "current",
                    "Ljava/util/HashMap$" + type + ";");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            mv.visitFieldInsn(PUTFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "iter",
                    "Ljava/util/Iterator;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "next" + type, "(I)Ljava/util/HashMap$" + type + ";",
                    "(I)Ljava/util/HashMap$" + type + "<TK;TV;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hashIter",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator", "expectedModCount", "I");
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ICMPEQ, l0);
            mv.visitTypeInsn(NEW, "java/util/ConcurrentModificationException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ConcurrentModificationException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l0);
            mv.visitFrame(F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hashIter",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "iter",
                    "Ljava/util/Iterator;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "java/util/HashMap$" + type);
            mv.visitFieldInsn(PUTFIELD, "java/util/HashMap$HashIterator", "current",
                    "Ljava/util/HashMap$" + type + ";");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hashIter",
                    "Ljava/util/HashMap$HashIterator;");
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator", "current",
                    "Ljava/util/HashMap$" + type + ";");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "hasNext", "()Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator$HashIteratorShuffler", "iter",
                    "Ljava/util/Iterator;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
