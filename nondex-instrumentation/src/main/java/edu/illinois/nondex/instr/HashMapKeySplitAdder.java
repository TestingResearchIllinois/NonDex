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
        MethodVisitor methodVisitor = super.visitMethod(
                Opcodes.ACC_PUBLIC,
                "forEachRemaining",
                "(Ljava/util/function/Consumer;)V",
                "(Ljava/util/function/Consumer<-TK;>;)V",
                null
        );
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(
                Opcodes.GETFIELD,
                "java/util/HashMap$KeySpliterator",
                "shuffler",
                "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;"
        );
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler",
                "forEachRemaining",
                "(Ljava/util/function/Consumer;)V",
                false
        );
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(Opcodes.RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable(
                "this",
                "Ljava/util/HashMap$KeySpliterator;",
                "Ljava/util/HashMap$KeySpliterator<TK;TV;>;",
                label0,
                label2,
                0
        );
        methodVisitor.visitLocalVariable(
                "action",
                "Ljava/util/function/Consumer;",
                "Ljava/util/function/Consumer<-TK;>;",
                label0,
                label2,
                1
        );
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    public void addTryAdvance() {
        MethodVisitor methodVisitor = super.visitMethod(
                Opcodes.ACC_PUBLIC,
                "tryAdvance",
                "(Ljava/util/function/Consumer;)Z",
                "(Ljava/util/function/Consumer<-TK;>;)Z",
                null
        );
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(
                Opcodes.GETFIELD,
                "java/util/HashMap$KeySpliterator",
                "shuffler",
                "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;"
        );
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler",
                "tryAdvance",
                "(Ljava/util/function/Consumer;)Z",
                false
        );
        methodVisitor.visitInsn(Opcodes.IRETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable(
                "this",
                "Ljava/util/HashMap$KeySpliterator;",
                "Ljava/util/HashMap$KeySpliterator<TK;TV;>;",
                label0,
                label1,
                0
        );
        methodVisitor.visitLocalVariable(
                "action",
                "Ljava/util/function/Consumer;",
                "Ljava/util/function/Consumer<-TK;>;",
                label0,
                label1,
                1
        );
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    @Override
    public void visitEnd() {

        addSplitShufflerField();
        addForEachRemaining();
        addTryAdvance();

        super.visitInnerClass(
                "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler",
                "java/util/HashMap$KeySpliterator",
                "KeySpliteratorShuffler",
                0
        );
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String desc, String signature, String[] exceptions
    ) {
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitTypeInsn(
                                Opcodes.NEW,
                                "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler"
                        );
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(
                                Opcodes.INVOKESPECIAL,
                                "java/util/HashMap$KeySpliterator$KeySpliteratorShuffler",
                                "<init>",
                                "(Ljava/util/HashMap$KeySpliterator;)V",
                                false
                        );
                        super.visitFieldInsn(
                                Opcodes.PUTFIELD,
                                "java/util/HashMap$KeySpliterator",
                                "shuffler",
                                "Ljava/util/HashMap$KeySpliterator$KeySpliteratorShuffler;"
                        );
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("forEachRemaining".equals(name)) {
            // Rename the existing method to original_forEachRemaining
            return super.visitMethod(access, "original_forEachRemaining", desc, signature, exceptions);
        }
        if ("tryAdvance".equals(name)) {
            return super.visitMethod(access, "original_tryAdvance", desc, signature, exceptions);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
