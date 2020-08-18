package com.melody.nettyserver.http;

/**
 * @author zqhuangc
 */
public abstract class MServlet {

    public void doGet(MRequest request, MResponse response){}
    public void doPost(MRequest request, MResponse response){}
}
