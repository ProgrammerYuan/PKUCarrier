/**
 *
 */
package com.programmeryuan.PKUCarrier.utils;

import android.util.Log;

/**
 * 输出调试信息
 *
 * @author Michael
 */
public class Logger {
    /**
     * 调试开关
     */
    static boolean enabled = false;

    /**
     * 在Logcat输出对象的值
     *
     * @param o 目标对象
     */
    public static void out(Object o) {
        String[] gen = gen(o);
        if (gen != null) {
            Log.i("", gen[0]);
            Log.i("", gen[1]);
        }
    }

    /**
     * 在Logcat输出对象的值，链接定位向上一层
     *
     * @param o 目标对象
     */
    public static void outUpper(Object o) {
        String[] gen = gen(o, 1);
        if (gen != null) {
            Log.i("", gen[0]);
            Log.i("", gen[1]);
        }
    }

    /**
     * 在Logcat输出对象的值，使用ERROR Level
     *
     * @param o 目标对象
     */
    public static void err(Object o) {
        String[] gen = gen(o);
        if (gen != null) {
            Log.e("", gen[0]);
            Log.e("", gen[1]);
        }
    }

    /**
     * 生成输出数据
     *
     * @param o 目标对象
     * @return 长度为2的String数组。内容为{调用位置，对象值}
     */
    static String[] gen(Object o) {
        return gen(o, 0);
    }

    /**
     * 生成输出数据。
     *
     * @param o     目标对象
     * @param level 调用栈的偏移值。必须是正数，例如：“1”即链接到调用位置的上一层调用位置
     * @return 长度为2的String数组。内容为{调用位置，对象值}
     */
    static String[] gen(Object o, int level) {
        String[] s = new String[2];
        try {
            if (enabled) {
                StackTraceElement[] stackTraceElement = Thread.currentThread()
                        .getStackTrace();
                int currentIndex = -1;
                for (int i = 0; i < stackTraceElement.length; i++) {
                    String name = stackTraceElement[i].getMethodName();
                    if (name.equalsIgnoreCase("out") || name.equalsIgnoreCase("err") || name.equalsIgnoreCase("outUpper")) {
                        currentIndex = i + 1 + level;
                        break;
                    }
                }
                String fullClassName = stackTraceElement[currentIndex].getClassName();
                String className = stackTraceElement[currentIndex].getFileName();
                String lineNumber = String
                        .valueOf(stackTraceElement[currentIndex].getLineNumber());
                s[0] = fullClassName + "『(" + className + ":" + lineNumber + ")』";
                if (o != null) {
                    s[1] = o.toString();
                } else {
                    s[1] = "null";
                }
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 设置是否开启输出
     *
     * @param b 开启
     */
    public static void setEnable(boolean b) {
        enabled = b;
    }

    /**
     * 获得开启状态
     *
     * @return 是否开启输出
     */
    public static boolean isEnabled() {
        return enabled;
    }
}