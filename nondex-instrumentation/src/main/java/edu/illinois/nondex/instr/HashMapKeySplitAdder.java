package edu.illinois.nondex.instr;

import org.objectweb.asm.*;

public class HashMapKeySplitAdder extends ClassVisitor {
    public HashMapKeySplitAdder(ClassVisitor ca) {
        super(Opcodes.ASM9, ca);
    }

    public void addSplitShufflerField() {
        FieldVisitor fv = super.visitField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "shuffler",
                "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;",
                "Ljava/util/HashMap<TK;TV;>.KeySpliterator.KeySpliteratorShuffler;",
                null
        );
        fv.visitEnd();
    }

    public void addForEachRemaining() {
        MethodVisitor methodVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "forEachRemaining", "(Ljava/util/function/Consumer;)V", "(Ljava/util/function/Consumer<-TK;>;)V", null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap$KeySpliterator", "shuffler", "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;");
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "forEachRemaining", "(Ljava/util/function/Consumer;)V", false);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(Opcodes.RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable("this", "Ljava/util/HashMap$KeySpliterator;", "Ljava/util/HashMap$KeySpliterator<TK;TV;>;", label0, label2, 0);
        methodVisitor.visitLocalVariable("action", "Ljava/util/function/Consumer;", "Ljava/util/function/Consumer<-TK;>;", label0, label2, 1);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    @Override
    public void visitEnd() {
        addSplitShufflerField();
        addForEachRemaining();
        super.visitInnerClass("java/util/HashMap$KeySpliterator$KeySpliteratorShuffler", "java/util/HashMap$KeySpliterator",
                "KeySpliteratorShuffler", 0);
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0); // Push "this" (KeySpliterator)
                        super.visitTypeInsn(Opcodes.NEW, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler"); // Create new KeySpliteratorShuffler
                        super.visitInsn(Opcodes.DUP); // Duplicate the reference to the new object
                        super.visitVarInsn(Opcodes.ALOAD, 0); // Push "this" again for the constructor
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler",
                                "<init>", "(Ljava/util/HashMap$KeySpliterator;)V", false); // Call constructor
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/HashMap$KeySpliterator", "shuffler",
                                "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;"); // Assign to shuffler
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("forEachRemaining".equals(name)) {
            // Rename the existing method to original_forEachRemaining
            return super.visitMethod(access, "original_forEachRemaining", desc, signature, exceptions);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
