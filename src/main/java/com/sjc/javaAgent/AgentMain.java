package com.sjc.javaAgent;


import com.sjc.javaAgent.command.ClassCommand;
import com.sjc.javaAgent.command.ThreadCommend;
import com.sjc.javaAgent.enhancer.AgentParam;
import com.sjc.javaAgent.enhancer.MyAdvice;
import com.sjc.javaAgent.enhancer.TimingAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/14
 * {@code @msg} reserved
 */
public class AgentMain {
    //premain
    public static void premain(String agentArgs, Instrumentation inst) {
        // 使用byteBuddy
        new AgentBuilder.Default()
                // 禁止byteBuddy 处理时修改类名
                .disableClassFormatChanges()
                //处理时使用retransform增强
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                //打印错误日志
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                //匹配哪些类 - controller 层
                .type(ElementMatchers.isAnnotatedWith(ElementMatchers.named("org.springframework.web.bind.annotation.RestController"))
                        .or(ElementMatchers.named("org.springframework.web.bind.annotation.Controller")))
                //增强，使用MyAdvice通知，对所有方法都进行增强
                .transform((builder, typeDescription, classLoader, moudle, protectionDomain) ->
                        builder.visit(Advice
                                .withCustomMapping()
                                        .bind(AgentParam.class , agentArgs)
                                .to(TimingAdvice.class).on(ElementMatchers.any()))
                )
                .installOn(inst);
    }

    //agentmain
    public static void agentmain(String agentArgs, Instrumentation inst) {
        //MemoryCommand.printMemory();
        //MemoryCommand.heapDump();
        //ThreadCommend.printThreadInfo();
        ClassCommand.printAllClassLoader(inst);
    }
}
