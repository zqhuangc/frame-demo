package com.melody.nettyrpc.provider;
import com.melody.nettyrpc.api.IRpcCall;

/**
 * @author zqhuangc
 */
public class RpcCall implements IRpcCall {

    @Override
    public String reply(String name) {
        return name + ":accept";
    }
}
