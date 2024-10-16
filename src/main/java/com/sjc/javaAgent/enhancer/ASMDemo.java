package com.sjc.javaAgent.enhancer;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.ICONST_0;


/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/16
 * {@code @msg} reserved
 */
public class ASMDemo {
    static String fileNmae = "";
    public static void main(String[] args) throws IOException {
        //从本地读取一个字节码文件byte[]
        byte[] bytes = FileUtils.readFileToByteArray(new File(fileNmae));

        //通过ASM修改字节码文件
        //将二进制文件转换成可以解析的内容
        ClassReader classReader = new ClassReader(bytes);

        //创建vistor对象，修改字节码信息
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(ASM7 , classWriter){

            // methodVistor 字节码中读取到method进行运行
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                //原始对象
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                //返回自定义MethodVistor
                MethodVisitor methodVisitor = new MethodVisitor(this.api , mv) {
                    //修改字节码指令

                    @Override
                    public void visitCode() {
                        // 插入一个字节码指令 ICONST_0
                        visitInsn(ICONST_0);
                    }
                };
                return methodVisitor;
            }
        };

        classReader.accept(classVisitor , 0);

        //将修改完的字节码信息写入文件，进行替换
        FileUtils.writeByteArrayToFile(new File(fileNmae ) , classWriter.toByteArray());
    }
}
