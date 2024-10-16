package com.sjc.javaAgent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/14
 * {@code @msg} reserved
 */
public class AttachMain {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        //获取进程列表，让用户手动进行输入
        //执行jps命令，打印所有进程列表
        Process jps = Runtime.getRuntime().exec("jps");
        //包装缓存流
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jps.getInputStream()));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        //输入进程id
        Scanner scanner = new Scanner(System.in);
        String processId = scanner.next();
        //获取进程虚拟机对象
        VirtualMachine vm = VirtualMachine.attach(processId);
        //执行java agent里边的agentmain方法
        vm.loadAgent("/Users/sunnyday/Library/Mobile Documents/com~apple~CloudDocs/mass/agentDemo1/target/agentDemo1-1.0-SNAPSHOT-jar-with-dependencies.jar");
    }
}
