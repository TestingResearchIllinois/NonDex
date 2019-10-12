package edu.illinois.nondex.instr;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;

public class HashMapVisitor extends ClassVisitor {

    ClassVisitor cw;
    public HashMapVisitor(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);
        cw = ca;
    }
    @Override
    public void visitEnd(){
        cw.visitField(ACC_PUBLIC, "dummy", "Ljava/lang/String;", null, null).visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, final String desc, final String signature, String[] exceptions) {
        if ("<init>".equals(name) &&  "()V".equals(desc)) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
                        mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
                        mv.visitFieldInsn(PUTFIELD, "Field", "dummy",
                        "Ljava/lang/String;");
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}