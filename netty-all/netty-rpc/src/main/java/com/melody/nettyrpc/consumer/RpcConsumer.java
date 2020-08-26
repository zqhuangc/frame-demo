package com.melody.nettyrpc.consumer;


import com.melody.nettyrpc.api.IRpcCalc;
import com.melody.nettyrpc.api.IRpcCall;

/**
 * @author zqhuangc
 */
public class RpcConsumer {

    public static void main(String[] args) {
        IRpcCall rpcOne = RpcProxy.create(IRpcCall.class);
        String r = rpcOne.reply("jerry");
        System.out.println(r);


        int a = 8,b = 2;
        IRpcCalc calc = RpcProxy.create(IRpcCalc.class);
        System.out.println(a + " + " + b +" = " + calc.add(a, b));
        System.out.println(a + " - " + b +" = " + calc.sub(a, b));
        System.out.println(a + " * " + b +" = " + calc.mult(a, b));
        System.out.println(a + " / " + b +" = " + calc.div(a, b));
    }
}
