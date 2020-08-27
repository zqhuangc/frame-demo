package com.melody.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * @author zqhuangc
 */
public class QHHandlerAdapter {

    protected Map<String,Integer> adapterMapping;

    public QHHandlerAdapter(Map<String, Integer> handlerAdapter) {
        this.adapterMapping = handlerAdapter;
    }

    //主要目的是用反射调用url对应的method
    public QHModelAndView handle(HttpServletRequest request, HttpServletResponse response, QHHandler handler)throws Exception{
        //为什么要传request、response、handler
        Class<?>[] parameterTypes = handler.method.getParameterTypes();

        // 通过索引找到具体参数
        Object[] paramValues = new Object[parameterTypes.length];

        Map<String,String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String,String[]> param:parameterMap.entrySet()) {
            // 参数多个值
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]", "")
                    .replaceAll(",\\s", ",");

            if(!this.adapterMapping.containsKey(param.getKey())){
                continue;
            }

            int index = this.adapterMapping.get(param.getKey());
            paramValues[index] = castStringValue(value,parameterTypes[index]);

        }

        //request 和 response 要赋值
        String req = HttpServletRequest.class.getName();
        if(this.adapterMapping.containsKey(req)){
            int reqIndex = this.adapterMapping.get(req);
            paramValues[reqIndex] = request;
        }

        String resp = HttpServletResponse.class.getName();
        if(this.adapterMapping.containsKey(resp)){
            int respIndex = this.adapterMapping.get(resp);
            paramValues[respIndex] = response;
        }

        boolean isModelAndView = handler.method.getReturnType() == QHModelAndView.class;
        Object r = handler.method.invoke(handler.controller, paramValues);

        if(isModelAndView){
            return (QHModelAndView) r;
        }else {
            return null;
        }


    }

    private Object castStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            return Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else{
            return null;
        }
    }


}
