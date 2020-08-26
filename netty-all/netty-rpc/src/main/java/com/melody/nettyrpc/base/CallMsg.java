package com.melody.nettyrpc.base;

import java.io.Serializable;

/**
 * 服务调用信息
 * @author zqhuangc
 */
public class CallMsg implements Serializable {

    private String className;    // 服务名称
    private String methodName;   // 被调用方法
    private Class<?>[] params;   // 参数列表
    private Object[] values;     // 参数值

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public void setParams(Class<?>[] params) {
        this.params = params;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
