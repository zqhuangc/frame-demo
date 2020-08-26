package com.melody.nettyrpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 接收处理
 * @author zqhuangc
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

    private Object result;

    public Object getResult(){
        return this.result;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o){
        this.result = o;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
    }
}
