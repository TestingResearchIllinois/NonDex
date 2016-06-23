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
    private String baseName;

    private MethodProperties hasNextProp;
    private MethodProperties nextTypeProp;

    private class MethodProperties {
        private int access;
        private String name;
        private String desc;
        private String signature;
        private String[] exceptions;
    }


    public HashMapShufflingAdder(ClassVisitor ca, String type, String baseName) {
        super(Opcodes.ASM5, ca);

        hasNextProp = new MethodProperties();
        nextTypeProp = new MethodProperties();

        this.type = type;
        this.baseName = baseName;
    }

    public FieldVisitor addShufflerType() {
        FieldVisitor fv = super.visitField(0, "shuffler",
                "L" + baseName + "$HashIterator$HashIteratorShuffler;",
                "L" + baseName + "<TK;TV;>.HashIterator.HashIteratorShuffler;", null);
        fv.visitEnd();
        return fv;
    }

    public void addNextType() {
        MethodVisitor mv = super.visitMethod(nextTypeProp.access, nextTypeProp.name,
                nextTypeProp.desc, nextTypeProp.signature, nextTypeProp.exceptions);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, baseName + "$HashIterator",
                "shuffler", "L" + baseName + "$HashIterator$HashIteratorShuffler;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, baseName + "$HashIterator",
                "this$0", "L" + baseName + ";");
        mv.visitFieldInsn(Opcodes.GETFIELD, baseName, "modCount", "I");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, baseName + "$HashIterator$HashIteratorShuffler",
                "next" + type, "(I)L" + baseName + "$" + type + ";", false);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    public void addHasNext() {
        MethodVisitor mv = super.visitMethod(hasNextProp.access, hasNextProp.name,
                hasNextProp.desc, hasNextProp.signature, hasNextProp.exceptions);

        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);

        mv.visitFieldInsn(Opcodes.GETFIELD, baseName + "$HashIterator",
                "shuffler", "L" + baseName + "$HashIterator$HashIteratorShuffler;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, baseName + "$HashIterator$HashIteratorShuffler",
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

        super.visitInnerClass(baseName + "$HashIterator$HashIteratorShuffler",
                baseName + "$HashIterator", "HashIteratorShuffler", 0);

        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if ("java/util/WeakHashMap".equals(baseName) && ("expectedModCount".equals(name) || "entry".equals(name))) {
            return super.visitField(access - Opcodes.ACC_PRIVATE, name, desc, signature, value);
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitTypeInsn(Opcodes.NEW, baseName + "$HashIterator$HashIteratorShuffler");
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL,
                                baseName + "$HashIterator$HashIteratorShuffler",
                                "<init>", "(L" + baseName + "$HashIterator;L" + baseName + "$HashIterator;)V", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, baseName + "$HashIterator",
                                "shuffler", "L" + baseName + "$HashIterator$HashIteratorShuffler;");
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

            return super.visitMethod(access, "original_hasNext", desc, signature, exceptions);
        }
        if (name.equals("next" + type)) {
            nextTypeProp.access = access;
            nextTypeProp.name = name;
            nextTypeProp.desc = desc;
            nextTypeProp.signature = signature;
            nextTypeProp.exceptions = exceptions;

            if (baseName.equals("java/util/WeakHashMap")) {
                return new MethodVisitor(Opcodes.ASM5,
                        super.visitMethod(access, "original_next" + type, desc, signature, exceptions)) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        if ("hasNext".equals(name)) {
                            super.visitMethodInsn(opcode, owner, "original_hasNext", desc, itf);
                        } else {
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    }
                };
            } else {
                return super.visitMethod(access, "original_next" + type, desc, signature, exceptions);
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
