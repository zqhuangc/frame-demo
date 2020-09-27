package com.melody.rpc.webservice;

import javax.jws.WebService;

/**
 * @author zqhuangc
 */
@WebService
public class EchoServiceImpl implements IEchoService {

    public String echo(String message) {
        System.out.println("call echo()");
        return "echo message：" + message;
    }
}
