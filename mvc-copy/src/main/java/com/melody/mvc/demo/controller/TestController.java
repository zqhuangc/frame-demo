package com.melody.mvc.demo.controller;


import com.melody.mvc.annocation.QHController;
import com.melody.mvc.annocation.QHRequestMapping;
import com.melody.mvc.annocation.QHRequestParam;
import com.melody.mvc.servlet.QHModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zqhuangc
 */
@QHController
@QHRequestMapping("/test")
public class TestController {

    @QHRequestMapping("/query/.*.json")
    public QHModelAndView query(HttpServletRequest request,HttpServletResponse response,
                                @QHRequestParam(value="name",required=false) String name,
                                @QHRequestParam("addr") String addr){
        //out(response,"get params name = " + name);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        model.put("addr", addr);
        return new QHModelAndView("first.qhl",model);
    }
    
    @QHRequestMapping("/add.json")
    public QHModelAndView add(HttpServletRequest req, HttpServletResponse resp,
                              @QHRequestParam(value = "id") Integer id){
        out(resp,"this is json string");
        return null;
    }
    

    public void out(HttpServletResponse response,String str){
        try {
            response.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
