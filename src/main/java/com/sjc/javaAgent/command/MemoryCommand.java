package com.sjc.javaAgent.command;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.List;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/16
 * {@code @msg} reserved
 */
public class MemoryCommand {
    //打印所有内存信息
    public static void printMemory () {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

        System.out.println("堆内存 ： ");
        //堆内存
        getMemoryInfo(memoryPoolMXBeans , MemoryType.HEAP);

        System.out.println("非堆内存 ： ");
        //非堆内存
        getMemoryInfo(memoryPoolMXBeans , MemoryType.NON_HEAP);

    }

    private static void getMemoryInfo (List<MemoryPoolMXBean> memoryPoolMXBeans , MemoryType type) {
        memoryPoolMXBeans.stream().filter(x -> x.getType().equals(type))
                .forEach(x -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("name:")
                            .append(x.getName())
                            .append("used:")
                            .append(x.getUsage().getUsed() / 1024 / 1024)
                            .append("m")

                            .append(" committed:")
                            .append(x.getUsage().getCommitted() / 1024 / 1024)
                            .append("m")

                            .append(" max:")
                            .append(x.getUsage().getMax() / 1024 /1024)
                            .append("m");
                    System.out.println(sb);
                });
    }
}
