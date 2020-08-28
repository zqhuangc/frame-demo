package com.melody.rpc.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author zqhuangc
 */
@WebService //SE和SEI的实现类
public interface IEchoService {

    @WebMethod //SEI中的方法
    String echo(String message);
}
