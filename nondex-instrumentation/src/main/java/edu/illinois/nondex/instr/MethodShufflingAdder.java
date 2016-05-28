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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodShufflingAdder extends ClassVisitor {

    public MethodShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if ("getExceptionTypes".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.CHECKCAST) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "java/lang/reflect/Method",
                                "exceptionTypes", "[Ljava/lang/Class;");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object",
                                "hashCode", "()I", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "edu/illinois/nondex/shuffling/ControlNondeterminism",
                                "shuffle", "([Ljava/lang/Object;I)[Ljava/lang/Object;", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("getGenericExceptionTypes".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ARETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/reflect/Executable",
                                "getGenericExceptionTypes", "()[Ljava/lang/reflect/Type;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism",
                                "shuffle", "([Ljava/lang/Object;I)[Ljava/lang/Object;", false);
                        super.visitTypeInsn(Opcodes.CHECKCAST, "[Ljava/lang/reflect/Type;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("getDeclaredAnnotations".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ARETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/reflect/Executable",
                                "getDeclaredAnnotations", "()[Ljava/lang/annotation/Annotation;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                                "([Ljava/lang/Object;I)[Ljava/lang/Object;", false);
                        super.visitTypeInsn(Opcodes.CHECKCAST, "[Ljava/lang/annotation/Annotation;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        if ("getParameterAnnotations".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ARETURN) {
                        super.visitVarInsn(Opcodes.ASTORE, 1);
                        super.visitInsn(Opcodes.ICONST_0);
                        super.visitVarInsn(Opcodes.ISTORE, 2);
                        Label l0 = new Label();
                        super.visitLabel(l0);
                        super.visitFrame(Opcodes.F_APPEND, 2, new Object[]{
                            "[[Ljava/lang/annotation/Annotation;", Opcodes.INTEGER}, 0, null);
                        super.visitVarInsn(Opcodes.ILOAD, 2);
                        super.visitVarInsn(Opcodes.ALOAD, 1);
                        super.visitInsn(Opcodes.ARRAYLENGTH);
                        Label l1 = new Label();
                        super.visitJumpInsn(Opcodes.IF_ICMPGE, l1);
                        super.visitVarInsn(Opcodes.ALOAD, 1);
                        super.visitVarInsn(Opcodes.ILOAD, 2);
                        super.visitVarInsn(Opcodes.ALOAD, 1);
                        super.visitVarInsn(Opcodes.ILOAD, 2);
                        super.visitInsn(Opcodes.AALOAD);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "hashCode", "()I", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "edu/illinois/nondex/shuffling/ControlNondeterminism", "shuffle",
                                "([Ljava/lang/Object;I)[Ljava/lang/Object;", false);
                        super.visitTypeInsn(Opcodes.CHECKCAST, "[Ljava/lang/annotation/Annotation;");
                        super.visitInsn(Opcodes.AASTORE);
                        super.visitIincInsn(2, 1);
                        super.visitJumpInsn(Opcodes.GOTO, l0);
                        super.visitLabel(l1);
                        super.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
                        super.visitVarInsn(Opcodes.ALOAD, 1);
                    }
                    super.visitInsn(opcode);
                }
            };
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}

