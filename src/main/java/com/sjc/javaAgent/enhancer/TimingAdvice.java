package com.sjc.javaAgent.enhancer;

import net.bytebuddy.asm.Advice;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/16
 * {@code @msg} reserved
 */
//统计耗时，打印方法名，类名
public class TimingAdvice {

    /**
     * 方法进入时候执行
     * @return 返回方法开始时候的时间
     */
    @Advice.OnMethodEnter
    public static long enter ( ) {
        return System.nanoTime();
    }

    /**
     * 方法退出时候执行
     * @param value 上面enter中的返回值
     */
    @Advice.OnMethodExit
    public static void exit (@Advice.Enter long value,
                             @Advice.Origin("#t") String className,
                             @Advice.Origin("#m") String methodName,
                             @AgentParam("agent.log") String logName){
        String str = methodName + "@" + className + "耗时为" + (System.nanoTime() - value) + "纳秒\n";
        try {
            FileUtils.writeStringToFile(new File(logName) , str , StandardCharsets.UTF_8 , true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
