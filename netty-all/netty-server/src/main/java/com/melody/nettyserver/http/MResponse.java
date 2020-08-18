package com.melody.nettyserver.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义响应
 * @author zqhuangc
 */
public class MResponse {

    private ChannelHandlerContext ctx;
    private HttpRequest request;
    private static Map<Integer, HttpResponseStatus> statusMap = new HashMap<>();

    static {
        statusMap.put(200, HttpResponseStatus.OK);
        statusMap.put(404, HttpResponseStatus.NOT_FOUND);
    }

    public MResponse(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void write(String out, Integer status){
        try {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    statusMap.get(status),
                    Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.EXPIRES, 0);

            if(HttpUtil.isKeepAlive(request)){
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            ctx.flush();
        }

    }
}
