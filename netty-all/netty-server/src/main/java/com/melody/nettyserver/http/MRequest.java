package com.melody.nettyserver.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * 自定义请求
 * @author zqhuangc
 */
public class MRequest {

    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public MRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String getUri(){
        return request.uri();
    }

    public String getMethod(){
        return request.method().name();
    }

    /**
     * 参数获取
     * @return
     */
    public Map<String, List<String>> getParameters(){
        QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
        return decoderQuery.parameters();
    }

    public String getParameter(String name){
        Map<String, List<String>> params = getParameters();
        List<String> param = params.get(name);
        if(null != param){
            return param.get(0);
        }else{
            return null;
        }
    }


}
