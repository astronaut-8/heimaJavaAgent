package com.sjc.javaAgent.enhancer;

import net.bytebuddy.asm.Advice;

/**
 * @author abstractMoonAstronaut
 * {@code @date} 2024/10/16
 * {@code @msg} reserved
 */
public class MyAdvice {

    /**
     * 方法进入时候执行
     * @param ary 获取到的全部参数
     * @return 返回方法开始时候的时间
     */
    @Advice.OnMethodEnter
    public static long enter ( @Advice.AllArguments Object[] ary) {
        if (ary != null ) {
            for (int i = 0; i < ary.length; i++) {
                System.out.println("参数:" + i + "内容" + ary[i]);
            }
        }
        return System.nanoTime();
    }

    /**
     * 方法退出时候执行
     * @param value 上面enter中的返回值
     */
    @Advice.OnMethodExit
    public static void exit (@Advice.Enter long value) {
        System.out.println("耗时为: " + (System.nanoTime() - value) + "纳秒");
    }
}
