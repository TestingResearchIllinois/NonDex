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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class HashMapShufflingAdder extends ClassVisitor {

    private String type;

    public HashMapShufflingAdder(ClassVisitor ca, String type) {
        super(Opcodes.ASM5, ca);
        this.type = type;
    }

    public FieldVisitor addShufflerType() {

        FieldVisitor fv = super.visitField(0, "shuffler", "Ljava/util/HashMap$HashIterator$HashIteratorShuffler;",
                "Ljava/util/HashMap<TK;TV;>.HashIterator.HashIteratorShuffler;", null);
        fv.visitEnd();
        return fv;
    }

    public void addNextType() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "next" + type, "()Ljava/util/HashMap$" + type + ";",
                "()Ljava/util/HashMap$" + type + "<TK;TV;>;", null);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap$HashIterator", "shuffler",
                "Ljava/util/HashMap$HashIterator$HashIteratorShuffler;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap$HashIterator", "this$0", "Ljava/util/HashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap", "modCount", "I");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap$HashIterator$HashIteratorShuffler", "next" + type,
                "(I)Ljava/util/HashMap$" + type + ";", false);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    public void addHasNext() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, "hasNext", "()Z", null, null);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);

        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap$HashIterator", "shuffler",
                "Ljava/util/HashMap$HashIterator$HashIteratorShuffler;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap$HashIterator$HashIteratorShuffler", "hasNext", "()Z",
                false);

        mv.visitInsn(Opcodes.IRETURN);

        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {

        {
            super.visitField(Opcodes.ACC_PUBLIC, "dummy", "Ljava/lang/String;", null, null).visitEnd();
        }
        addShufflerType();
        addNextType();

        addHasNext();

        super.visitInnerClass("java/util/HashMap$HashIterator$HashIteratorShuffler", "java/util/HashMap$HashIterator",
                "HashIteratorShuffler", 0);

        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitTypeInsn(Opcodes.NEW, "java/util/HashMap$HashIterator$HashIteratorShuffler");
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap$HashIterator$HashIteratorShuffler",
                                "<init>", "(Ljava/util/HashMap$HashIterator;Ljava/util/HashMap$HashIterator;)V", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/HashMap$HashIterator", "shuffler",
                                "Ljava/util/HashMap$HashIterator$HashIteratorShuffler;");

                        //super.visitVarInsn(Opcodes.ALOAD, 0);
                        //super.visitVarInsn(Opcodes.ALOAD, 1);
                        //super.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap", "dummy", "Ljava/lang/String;");
                        //super.visitFieldInsn(Opcodes.PUTFIELD, "java/util/HashMap$HashIterator", "dummy", "Ljava/lang/String;");


                        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap", "dummy", "Ljava/lang/String;");
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("hasNext".equals(name)) {
            return super.visitMethod(access, "original_hasNext", desc, signature, exceptions);
        }
        if (name.equals("next" + type)) {
            return super.visitMethod(access, "original_next" + type, desc, signature, exceptions);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
