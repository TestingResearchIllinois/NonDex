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

public class HashedMapShufflingAdder extends ClassVisitor {

    private String type;

    public HashedMapShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);
        this.type = type;
    }

    public FieldVisitor addShufflerType() {
        FieldVisitor fv = super.visitField(0, "shuffler",
                "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler;", null, null);
        fv.visitEnd();
        return fv;
    }

    public void addNextType() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PROTECTED, "nextEntry",
                "()Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;",
                "()Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry<TK;TV;>;", null);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator", "shuffler",
                "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
                "parent", "Lorg/apache/commons/collections4/map/AbstractHashedMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap", "modCount", "I");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
                "nextEntry", "(I)Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;", false);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    public void addHasNext() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
                "hasNext", "()Z", null, null);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
                "shuffler", "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
                "hasNext", "()Z", false);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        addShufflerType();
        addNextType();

        addHasNext();

        super.visitInnerClass("org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
                "org/apache/commons/collections4/map/AbstractHashedMap",
                "HashedMapIteratorShuffler", Opcodes.ACC_PROTECTED + Opcodes.ACC_STATIC);

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
                        super.visitTypeInsn(Opcodes.NEW,
                                "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler");
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL,
                                "org/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler",
                                "<init>", "(Lorg/apache/commons/collections4/map/AbstractHashedMap$HashIterator;)V",
                                false);
                        super.visitFieldInsn(Opcodes.PUTFIELD,
                                "org/apache/commons/collections4/map/AbstractHashedMap$HashIterator",
                                "shuffler",
                                "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashedMapIteratorShuffler;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("hasNext".equals(name)) {
            return super.visitMethod(access, "original_hasNext", desc, signature, exceptions);
        }
        if ("next".equals(name)) {
            return super.visitMethod(access, "original_next", desc, signature, exceptions);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {

        if ("last".equals(name)) {
            return super.visitField(Opcodes.ACC_PROTECTED, "last",
                    "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry;",
                    "Lorg/apache/commons/collections4/map/AbstractHashedMap$HashEntry<TK;TV;>;", null);
        }
        if ("expectedModCount".equals(name)) {
            return super.visitField(Opcodes.ACC_PROTECTED, "expectedModCount", "I", null, null);
        }
        return super.visitField(access, name, desc, signature, value);
    }
}
