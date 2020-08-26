package com.melody.nettyserver.server;

import com.melody.nettyserver.http.MRequest;
import com.melody.nettyserver.http.MResponse;
import com.melody.nettyserver.http.MServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.log4j.Logger;

import javax.core.config.CustomConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 请求处理
 * @author zqhuangc
 */
public class JerryMicHandler extends ChannelInboundHandlerAdapter {

    private Logger LOG = Logger.getLogger(JerryMicHandler.class);

    private static final Map<Pattern, Class<?>> servletMapping = new HashMap<>();

    static {
        CustomConfig.load("web.properties");

        for (String key : CustomConfig.getKeys()) {
            if(key.startsWith("servlet")){
                String name = key.replaceFirst("servlet.", "");
                if(name.contains(".")){
                    name = name.substring(0, name.indexOf("."));
                }else{
                    continue;
                }
                String pattern = CustomConfig.getString("servlet." + name + ".urlPattern");
                pattern = pattern.replaceAll("\\*", ".*");
                String className = CustomConfig.getString("servlet." + name + ".className");
                if(!servletMapping.containsKey(Pattern.compile(pattern))){
                    try {
                        servletMapping.put(Pattern.compile(pattern), Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            HttpRequest r = (HttpRequest) msg;
            MRequest request = new MRequest(ctx, r);
            MResponse response = new MResponse(ctx, r);
            String uri = request.getUri();
            String method = request.getMethod();

            LOG.info(String.format("Uri:%s method %s", uri, method));
            boolean hasPattern = false;

            for (Map.Entry<Pattern, Class<?>> entry : servletMapping.entrySet()) {
                if (entry.getKey().matcher(uri).matches()) {
                    MServlet servlet = (MServlet) entry.getValue().getDeclaredConstructor().newInstance();
                    if ("get".equalsIgnoreCase(method)) {
                        servlet.doGet(request, response);
                    } else {
                        servlet.doPost(request, response);
                    }
                    hasPattern = true;
                }

                if (!hasPattern){
                    String out = String.format("404 NotFound URL%s for method %s", uri,method);
                    response.write(out, 404);
                    return;
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
