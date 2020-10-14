package com.melody.opensource.springbootelkdemo.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @description
 *
 * @author zqhuangc
 */
public abstract class ExceptionUtil {
    /**
     * 获取堆栈信息
     */
    public static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}
