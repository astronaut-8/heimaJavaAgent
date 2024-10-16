package com.sjc.javaAgent;

import com.sjc.javaAgent.command.MemoryCommand;

import java.lang.instrument.Instrumentation;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/14
 * {@code @msg} reserved
 */
public class AgentMain {
    //premain
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain functions");
    }

    //agentmain
    public static void agentmain(String agentArgs, Instrumentation inst) {
        //MemoryCommand.printMemory();
        MemoryCommand.heapDump();
    }
}
