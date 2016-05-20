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

public class AddShufflingToHashMap extends ClassVisitor {

    private class MethodProperties {
        int access;
        String name;
        String desc;
        String signature;
        String[] exceptions;
    }

    MethodProperties hasNextProp;
    MethodProperties nextNodeProp;

    public AddShufflingToHashMap(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);

        hasNextProp = new MethodProperties();
        nextNodeProp = new MethodProperties();
    }

    public FieldVisitor add_shuffler() {
        FieldVisitor fv = super.visitField(0, "shuffler", "Ljava/util/HashIteratorShuffler;", null, null);
        fv.visitEnd();
        return fv;
    }

    public void add_nextNode() {
        MethodVisitor mv = super.visitMethod(nextNodeProp.access, nextNodeProp.name,
                nextNodeProp.desc, nextNodeProp.signature, nextNodeProp.exceptions);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap$HashIterator",
                "shuffler", "Ljava/util/HashIteratorShuffler;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap$HashIterator",
                "this$0", "Ljava/util/HashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap", "modCount", "I");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashIteratorShuffler",
                "nextNode", "(I)Ljava/util/HashMap$Node;", false);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    public void add_hasNext() {
        MethodVisitor mv = super.visitMethod(hasNextProp.access, hasNextProp.name,
                hasNextProp.desc, hasNextProp.signature, hasNextProp.exceptions);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/HashMap$HashIterator",
                "shuffler", "Ljava/util/HashIteratorShuffler;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashIteratorShuffler",
                "hasNext", "()Z", false);

        mv.visitInsn(Opcodes.IRETURN);

        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        add_shuffler();
        add_nextNode();
        add_hasNext();

        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if ("<init>".equals(name) || "HashMap$HashIterator".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        this.visitVarInsn(Opcodes.ALOAD, 0);
                        this.visitTypeInsn(Opcodes.NEW, "java/util/HashIteratorShuffler");
                        this.visitInsn(Opcodes.DUP);
                        this.visitVarInsn(Opcodes.ALOAD, 0);
                        this.visitMethodInsn(Opcodes.INVOKESPECIAL,
                                "java/util/HashIteratorShuffler",
                                "<init>", "(Ljava/util/HashMap$HashIterator;)V", false);
                        this.visitFieldInsn(Opcodes.PUTFIELD, "java/util/HashMap$HashIterator",
                                "shuffler", "Ljava/util/HashIteratorShuffler;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("hasNext".equals(name)) {
            hasNextProp.access = access;
            hasNextProp.name = name;
            hasNextProp.desc = desc;
            hasNextProp.signature = signature;
            hasNextProp.exceptions = exceptions;

            MethodVisitor original = super.visitMethod(access, "original_hasNext", desc, signature, exceptions);

            return original;
        }
        if ("nextNode".equals(name)) {
            nextNodeProp.access = access;
            nextNodeProp.name = name;
            nextNodeProp.desc = desc;
            nextNodeProp.signature = signature;
            nextNodeProp.exceptions = exceptions;

            MethodVisitor original = super.visitMethod(access, "original_nextNode", desc, signature, exceptions);

            return original;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
