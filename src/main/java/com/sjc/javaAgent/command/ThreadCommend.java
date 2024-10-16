package com.sjc.javaAgent.command;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/16
 * {@code @msg} reserved
 */
public class ThreadCommend {

    //获取线程运行信息
    public static void printThreadInfo () {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //是否支持监视器和同步器
        //重载方法 有第三个参数 指定栈的深度
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(threadMXBean.isObjectMonitorUsageSupported(),
                threadMXBean.isSynchronizerUsageSupported());
        for (ThreadInfo threadInfo : threadInfos) {
            StringBuilder sb = new StringBuilder();
            sb.append("name :")
                    .append(threadInfo.getThreadName())
                    .append( "threadId: ")
                    .append(threadInfo.getThreadId())
                    .append(" threadState: ")
                    .append(threadInfo.getThreadState()
                    );

            System.out.println(sb);
            //打印栈信息
            StackTraceElement[] stackTrace = threadInfo.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                System.out.println(stackTraceElement);
            }
        }
    }
}
