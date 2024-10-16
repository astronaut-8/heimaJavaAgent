package com.sjc.javaAgent.enhancer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/16
 * {@code @msg} reserved
 */
public class AsmEnhancer {

    public static byte[] enhanceCLass (byte[] bytes) {
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(ASM7, classWriter){
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);

                //这个增强方法不屑了，太难了，写不了一点
                //return new MyMethodVisitor(this.api , methodVisitor);
                return null;
            }
        };
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classVisitor , 0);
        return classWriter.toByteArray();
    }
}
