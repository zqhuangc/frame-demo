package com.melody.rpc.webservice;

import javax.xml.ws.Endpoint;

/**
 * @author zqhuangc
 */
public class WebServiceBootstrap {

    public static void main(String[] args) {

        Endpoint.publish("http://localhost:8080/echo",new EchoServiceImpl());

        System.out.println("publish success");
    }
}
