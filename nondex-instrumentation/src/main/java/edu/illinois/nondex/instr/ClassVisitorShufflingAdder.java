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

import java.util.HashSet;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class ClassVisitorShufflingAdder extends ClassVisitor {
    private static final HashSet<String> apisReturningShufflableArrays = new HashSet<>();
    private String cn;


    static {
        apisReturningShufflableArrays.add("java/lang/Class.getDeclaredMethods");
        apisReturningShufflableArrays.add("java/lang/Class.getClasses");
        apisReturningShufflableArrays.add("java/lang/Class.getFields");
        apisReturningShufflableArrays.add("java/lang/Class.getMethods");
        apisReturningShufflableArrays.add("java/lang/Class.getConstructors");
        apisReturningShufflableArrays.add("java/lang/Class.getDeclaredClasses");
        apisReturningShufflableArrays.add("java/lang/Class.getDeclaredFields");
        apisReturningShufflableArrays.add("java/lang/Class.getDeclaredMethods");
        apisReturningShufflableArrays.add("java/lang/Class.getDeclaredConstructors");
        apisReturningShufflableArrays.add("java/lang/Class.getAnnotations");
        apisReturningShufflableArrays.add("java/lang/Class.getDeclaredAnnotations");

        apisReturningShufflableArrays.add("java/lang/reflect/Field.getAnnotationsByType");
        apisReturningShufflableArrays.add("java/lang/reflect/Field.getDeclaredAnnotations");

        apisReturningShufflableArrays.add("java/io/File.list");
        apisReturningShufflableArrays.add("java/io/File.listFiles");
        apisReturningShufflableArrays.add("java/io/File.listRoots");

        apisReturningShufflableArrays.add("java/text/BreakIterator.getAvailableLocales");
        apisReturningShufflableArrays.add("java/text/Collator.getAvailableLocales");
        apisReturningShufflableArrays.add("java/text/DateFormat.getAvailableLocales");

        apisReturningShufflableArrays.add("java/text/DateFormatSymbols.getZoneStrings");

        apisReturningShufflableArrays.add("java/util/PriorityQueue.toArray");

    }

    public ClassVisitorShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM9, ca);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.cn = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {

        final String methodId = this.cn + "." + name;

        if (apisReturningShufflableArrays.contains(methodId)) {

            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions)) {

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ARETURN && "java/text/DateFormatSymbols.getZoneStrings".equals(methodId)) {
                        this.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism",
                                "extendZoneStrings", "([[Ljava/lang/String;)[[Ljava/lang/String;", false);
                    } else if (opcode == Opcodes.ARETURN) {
                        shuffleJustReturnedArray(desc);
                    }
                    super.visitInsn(opcode);
                }

                private void shuffleJustReturnedArray(String methodDescriptor) {
                    // Call the shuffle method which returns Object[]
                    this.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism",
                            "shuffle", "([Ljava/lang/Object;)[Ljava/lang/Object;", false);

                    // Extract the return type from the method descriptor
                    Type returnType = Type.getReturnType(methodDescriptor);
                    String returnTypeInternalName = returnType.getInternalName();

                    // Add CHECKCAST instruction to convert Object[] to the specific array type
                    // For File.listRoots() this converts [Ljava/lang/Object; to [Ljava/io/File;
                    this.visitTypeInsn(Opcodes.CHECKCAST, returnTypeInternalName);
                }
            };
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
