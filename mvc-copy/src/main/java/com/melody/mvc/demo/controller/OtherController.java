package com.melody.mvc.demo.controller;

import com.melody.mvc.annocation.QHAutowired;
import com.melody.mvc.annocation.QHController;
import com.melody.mvc.annocation.QHRequestMapping;
import com.melody.mvc.annocation.QHRequestParam;
import com.melody.mvc.demo.service.IQueryService;
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
@QHRequestMapping("/other")
public class OtherController {

    @QHAutowired
    private IQueryService queryService;

    @QHRequestMapping("/search/.*.json")
    public void search(HttpServletRequest request,HttpServletResponse response,
                       @QHRequestParam("name") String name){
        String result = queryService.search(name);
        out(response,result);
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
