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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This class instrument HashMap to add a new field called "initTraces",
 * which stores the stack traces when a HashMap is created (e.g., new HashMap())
 *
 * @author Peilun Zhang
 *
 */
public class HashMapVisitor extends ClassVisitor {

    ClassVisitor cw;

    public HashMapVisitor(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);
        cw = ca;
    }

    @Override
    public void visitEnd() {
        cw.visitField(Opcodes.ACC_PUBLIC, "initTraces", "Ljava/lang/String;", null, null).visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, final String desc, final String sign, String[] exceptions) {

        // This instrument all the constructors
        if ("<init>".equals(name)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, sign, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        // this.initTraces = Arrays.toString(Thread.currentThread().getStackTrace());
                        visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
                        mv.visitFieldInsn(Opcodes.PUTFIELD,
                                "java/util/HashMap", "initTraces", "Ljava/lang/String;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return super.visitMethod(access, name, desc, sign, exceptions);
    }
}