package com.melody.nettyserver.servlet;

import com.melody.nettyserver.http.MRequest;
import com.melody.nettyserver.http.MResponse;
import com.melody.nettyserver.http.MServlet;

/**
 * @author zqhuangc
 */
public class TestServlet extends MServlet {

    @Override
    public void doGet(MRequest request, MResponse response) {
        doPost(request, response);
    }

    @Override
    public void doPost(MRequest request, MResponse response) {
        String param = "name";
        String str = request.getParameter(param);
        response.write(param + ":" + str,200);
    }
}
