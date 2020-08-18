package com.melody.nettyserver.servlet;

import com.alibaba.fastjson.JSON;
import com.melody.nettyserver.http.MRequest;
import com.melody.nettyserver.http.MResponse;
import com.melody.nettyserver.http.MServlet;


/**
 * @author zqhuangc
 */
public class OneServlet extends MServlet {
    @Override
    public void doGet(MRequest request, MResponse response) {
        doPost(request, response);
    }

    @Override
    public void doPost(MRequest request, MResponse response) {
        String str = JSON.toJSONString(request.getParameters(),true);
        response.write(str,200);
    }
}
