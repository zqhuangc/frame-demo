package com.melody.mvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author zqhuangc
 */
public class QHHandler {
    protected Pattern pattern;
    protected Object controller;
    protected Method method;


    public QHHandler(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }
}
