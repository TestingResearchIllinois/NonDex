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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ConcurrentHashMapShufflingAdder extends ClassVisitor {

    public ConcurrentHashMapShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        Label l0 = new Label();
                        mv.visitJumpInsn(Opcodes.IFNULL, l0);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitInsn(Opcodes.ARRAYLENGTH);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Arrays", "copyOf",
                                "([Ljava/lang/Object;I)[Ljava/lang/Object;", false);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism",
                                "shuffle", "([Ljava/lang/Object;)[Ljava/lang/Object;", false);
                        mv.visitTypeInsn(Opcodes.CHECKCAST, "[Ljava/util/concurrent/ConcurrentHashMap$Node;");
                        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/concurrent/ConcurrentHashMap$Traverser", "tab",
                                "[Ljava/util/concurrent/ConcurrentHashMap$Node;");
                        mv.visitLabel(l0);
                        mv.visitFrame(Opcodes.F_FULL, 5,
                                new Object[]{
                                    "java/util/concurrent/ConcurrentHashMap$Traverser",
                                    "[Ljava/util/concurrent/ConcurrentHashMap$Node;", Opcodes.INTEGER,
                                    Opcodes.INTEGER, Opcodes.INTEGER},
                                0, new Object[]{});
                        mv.visitInsn(Opcodes.RETURN);
                    }
                    super.visitInsn(opcode);
                }
            };
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
