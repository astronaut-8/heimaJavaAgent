package com.sjc.javaAgent.command;

import com.sun.management.HotSpotDiagnosticMXBean;

import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

import java.text.SimpleDateFormat;
import java.util.Date;
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

        System.out.println("打印nio 相关内存");
        //打印nio相关内存
        try {
            Class clazz = Class.forName("java.lang.management.BufferPoolMXBean");
            List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(clazz);

            //打印内容
            for (BufferPoolMXBean bufferPoolMXBean : bufferPoolMXBeans) {
                StringBuilder sb = new StringBuilder();
                sb.append("name:")
                        .append(bufferPoolMXBean.getName())
                        .append("used:")
                        .append(bufferPoolMXBean.getMemoryUsed() / 1024 / 1024)
                        .append("m")

                        .append(" capacity:")
                        .append(bufferPoolMXBean.getTotalCapacity()/ 1024 / 1024)
                        .append("m");

                System.out.println(sb);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    public static void heapDump () {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        HotSpotDiagnosticMXBean platformMXBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);

        try {
            platformMXBean.dumpHeap(simpleDateFormat.format(new Date() + ".hprof") , true);//只保存存活对象
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
