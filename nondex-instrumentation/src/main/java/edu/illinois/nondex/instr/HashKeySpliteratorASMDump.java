package edu.illinois.nondex.instr;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class HashKeySpliteratorASMDump implements Opcodes {

    public static byte[] dump() {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "<K:Ljava/lang/Object;V:Ljava/lang/Object;>Ljava/lang/Object;", "java/lang/Object", null);

        classWriter.visitSource("HashMap$KeySpliterator$KeySpliteratorShuffler.java", null);

        classWriter.visitInnerClass("java/util/HashMap$KeySpliterator", "java/util/HashMap", "KeySpliterator", ACC_FINAL | ACC_STATIC);

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "iter", "Ljava/util/Iterator;", "Ljava/util/Iterator<TK;>;", null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "keySpliterator", "Ljava/util/HashMap$KeySpliterator;", "Ljava/util/HashMap$KeySpliterator<TK;TV;>;", null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/HashMap$KeySpliterator;)V", "(Ljava/util/HashMap$KeySpliterator<TK;TV;>;)V", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(12, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(13, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "keySpliterator", "Ljava/util/HashMap$KeySpliterator;");
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(16, label2);
            methodVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            methodVisitor.visitVarInsn(ASTORE, 2);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(17, label3);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "keySpliterator", "Ljava/util/HashMap$KeySpliterator;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitInvokeDynamicInsn("accept", "(Ljava/util/List;)Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKESTATIC, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "lambda$new$0", "(Ljava/util/List;Ljava/lang/Object;)V", false), Type.getType("(Ljava/lang/Object;)V")});
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap$KeySpliterator", "original_forEachRemaining", "(Ljava/util/function/Consumer;)V", false);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(20, label4);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle", "(Ljava/util/List;)Ljava/util/List;", false);
            methodVisitor.visitVarInsn(ASTORE, 3);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(23, label5);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            methodVisitor.visitFieldInsn(PUTFIELD, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "iter", "Ljava/util/Iterator;");
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(24, label6);
            methodVisitor.visitInsn(RETURN);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitLocalVariable("this", "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;", "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler<TK;TV;>;", label0, label7, 0);
            methodVisitor.visitLocalVariable("ks", "Ljava/util/HashMap$KeySpliterator;", "Ljava/util/HashMap$KeySpliterator<TK;TV;>;", label0, label7, 1);
            methodVisitor.visitLocalVariable("keys", "Ljava/util/List;", "Ljava/util/List<TK;>;", label3, label7, 2);
            methodVisitor.visitLocalVariable("res", "Ljava/util/List;", "Ljava/util/List<TK;>;", label5, label7, 3);
            methodVisitor.visitMaxs(2, 4);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "forEachRemaining", "(Ljava/util/function/Consumer;)V", "(Ljava/util/function/Consumer<-TK;>;)V", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(31, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "iter", "Ljava/util/Iterator;");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "forEachRemaining", "(Ljava/util/function/Consumer;)V", true);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(32, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;", "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler<TK;TV;>;", label0, label2, 0);
            methodVisitor.visitLocalVariable("action", "Ljava/util/function/Consumer;", "Ljava/util/function/Consumer<-TK;>;", label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "tryAdvance", "(Ljava/util/function/Consumer;)Z", "(Ljava/util/function/Consumer<-TK;>;)Z", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "iter", "Ljava/util/Iterator;");
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            Label label1 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label1);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "iter", "Ljava/util/Iterator;");
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            methodVisitor.visitVarInsn(ASTORE, 2);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitInsn(IRETURN);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitInsn(IRETURN);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLocalVariable("nextElement", "Ljava/lang/Object;", "TK;", label3, label1, 2);
            methodVisitor.visitLocalVariable("this", "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;", "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler<TK;TV;>;", label0, label5, 0);
            methodVisitor.visitLocalVariable("action", "Ljava/util/function/Consumer;", "Ljava/util/function/Consumer<-TK;>;", label0, label5, 1);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "lambda$new$0", "(Ljava/util/List;Ljava/lang/Object;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(17, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("keys", "Ljava/util/List;", null, label0, label1, 0);
            methodVisitor.visitLocalVariable("key", "Ljava/lang/Object;", null, label0, label1, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
