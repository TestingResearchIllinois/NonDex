package edu.illinois.nondex.instr;
import org.objectweb.asm.*;
public class HashIteratorShufflerEntryASMDump implements Opcodes {

    public static byte[] dump () {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "java/util/HashIteratorShufflerEntry", "<K:Ljava/lang/Object;V:Ljava/lang/Object;>Ljava/lang/Object;", "java/lang/Object", null);

        cw.visitInnerClass("java/util/HashMap$Entry", "java/util/HashMap", "Entry", ACC_STATIC);

        cw.visitInnerClass("java/util/HashMap$HashIterator", "java/util/HashMap", "HashIterator", ACC_ABSTRACT);

        {
            fv = cw.visitField(ACC_PRIVATE, "iter", "Ljava/util/Iterator;", "Ljava/util/Iterator<Ljava/util/HashMap$Entry<TK;TV;>;>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "hashIter", "Ljava/util/HashMap$HashIterator;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/HashMap$HashIterator;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "java/util/HashIteratorShufflerEntry", "hashIter", "Ljava/util/HashMap$HashIterator;");
            mv.visitTypeInsn(NEW, "java/util/ArrayList");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 3, new Object[] {"java/util/HashIteratorShufflerEntry", "java/util/HashMap$HashIterator", "java/util/List"}, 0, new Object[] {});
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashIteratorShufflerEntry", "hashIter", "Ljava/util/HashMap$HashIterator;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap$HashIterator", "original_hasNext", "()Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashIteratorShufflerEntry", "hashIter", "Ljava/util/HashMap$HashIterator;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap$HashIterator", "original_nextEntry", "()Ljava/util/HashMap$Entry;", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(POP);
            mv.visitJumpInsn(GOTO, l0);
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle", "(Ljava/util/List;)Ljava/util/List;", false);
            mv.visitInsn(POP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            mv.visitFieldInsn(PUTFIELD, "java/util/HashIteratorShufflerEntry", "iter", "Ljava/util/Iterator;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "nextEntry", "(I)Ljava/util/HashMap$Entry;", "(I)Ljava/util/HashMap$Entry<TK;TV;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashIteratorShufflerEntry", "hashIter", "Ljava/util/HashMap$HashIterator;");
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator", "expectedModCount", "I");
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ICMPEQ, l0);
            mv.visitTypeInsn(NEW, "java/util/ConcurrentModificationException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ConcurrentModificationException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashIteratorShufflerEntry", "hashIter", "Ljava/util/HashMap$HashIterator;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashIteratorShufflerEntry", "iter", "Ljava/util/Iterator;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "java/util/HashMap$Entry");
            mv.visitFieldInsn(PUTFIELD, "java/util/HashMap$HashIterator", "current", "Ljava/util/HashMap$Entry;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashIteratorShufflerEntry", "hashIter", "Ljava/util/HashMap$HashIterator;");
            mv.visitFieldInsn(GETFIELD, "java/util/HashMap$HashIterator", "current", "Ljava/util/HashMap$Entry;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "hasNext", "()Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "java/util/HashIteratorShufflerEntry", "iter", "Ljava/util/Iterator;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
