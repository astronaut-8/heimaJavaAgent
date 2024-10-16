package com.sjc.javaAgent.command;

import com.sjc.javaAgent.enhancer.AsmEnhancer;
import com.sjc.javaAgent.enhancer.MyAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/16
 * {@code @msg} reserved
 */
public class ClassCommand {

    //打印所有类加载器
     public static void printAllClassLoader(Instrumentation inst) {
         HashSet<ClassLoader> classLoaders = new HashSet<>();
         //获取所有的类
         Class[] allLoadedClasses = inst.getAllLoadedClasses();
         Arrays.stream(allLoadedClasses).forEach(
                 loadClass -> {
                     ClassLoader classLoader = loadClass.getClassLoader();
                     classLoaders.add(classLoader);
                 }
         );
         //打印类加载器
         String collect = classLoaders.stream().map(x -> {
             if (x == null) {
                 //启动类加载器
                 return "BootStrapClassLoader";
             }
             return x.getName();
         }).filter(Objects::nonNull).distinct().sorted(String::compareTo).collect(Collectors.joining(","));
         //去重过滤掉相同的反射优化的类加载器
         System.out.println(collect);
     }

    //打印类的源代码
    public static void printClassSourceCode (Instrumentation inst) {
        System.out.println("请输入类名: ");
        //输入类名
        Scanner scanner = new Scanner(System.in);
        String className = scanner.next();

        //根据类名找到class对象
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (allLoadedClass.getName().equals(className)) {
                //添加转换器
                ClassFileTransformer classFileTransformer = new ClassFileTransformer() {
                    // 类的转换器本来可以增强类信息，这边是起获取到类的字节码信息的功能
                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                        //通过jd-core反编译并打印源代码
                        try {
                            printJDCoreSourceCode(classfileBuffer , className);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return ClassFileTransformer.super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    }
                };
                inst.addTransformer(classFileTransformer , true);

                // 触发转换器
                try {
                    inst.retransformClasses(allLoadedClass);
                } catch (UnmodifiableClassException e) {
                    throw new RuntimeException(e);
                }finally {
                    //删除转换器
                    inst.removeTransformer(classFileTransformer);
                }
            }
        }
    }
    //通过jd-core打印源代码
    private static void printJDCoreSourceCode (byte[] bytes , String className) throws Exception {
         //load对象
        Loader loader = new Loader() {
            @Override
            public boolean canLoad(String s) {
                return true;
            }

            @Override
            public byte[] load(String s) throws LoaderException {
                return bytes;
            }
        };
        Printer printer = new Printer() {
            protected static final String TAB = "  ";
            protected static final String NEWLINE = "\n";

            protected int indentationCount = 0;
            protected StringBuilder sb = new StringBuilder();

            @Override public String toString() { return sb.toString(); }

            @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {}
            @Override public void end() {
                // 打印源代码
                System.out.println(sb);
            }

            @Override public void printText(String text) { sb.append(text); }
            @Override public void printNumericConstant(String constant) { sb.append(constant); }
            @Override public void printStringConstant(String constant, String ownerInternalName) { sb.append(constant); }
            @Override public void printKeyword(String keyword) { sb.append(keyword); }
            @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) { sb.append(name); }
            @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) { sb.append(name); }

            @Override public void indent() { this.indentationCount++; }
            @Override public void unindent() { this.indentationCount--; }

            @Override public void startLine(int lineNumber) { for (int i=0; i<indentationCount; i++) sb.append(TAB); }
            @Override public void endLine() { sb.append(NEWLINE); }
            @Override public void extraLine(int count) { while (count-- > 0) sb.append(NEWLINE); }

            @Override public void startMarker(int type) {}
            @Override public void endMarker(int type) {}
        };
        //通过jd-core 方法打印
        ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();

        decompiler.decompile(loader, printer, className);

    }

    public static void enhanceClass (Instrumentation inst) {
        System.out.println("请输入类名: ");
        //输入类名
        Scanner scanner = new Scanner(System.in);
        String className = scanner.next();

        //根据类名找到class对象
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (allLoadedClass.getName().equals(className)) {
               // 使用byteBuddy
                new AgentBuilder.Default()
                        // 禁止byteBuddy 处理时修改类名
                        .disableClassFormatChanges()
                        //处理时使用retransform增强
                        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                        //打印错误日志
                        .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                        //匹配哪些类
                        .type(ElementMatchers.named(className))
                        //增强，使用MyAdvice通知，对所有方法都进行增强
                        .transform((builder, typeDescription, classLoader, moudle, protectionDomain) ->
                            builder.visit(Advice.to(MyAdvice.class).on(ElementMatchers.any()))
                        )
                        .installOn(inst);

            }
        }
    }
}
