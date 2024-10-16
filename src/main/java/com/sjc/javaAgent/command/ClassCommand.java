package com.sjc.javaAgent.command;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
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
}
