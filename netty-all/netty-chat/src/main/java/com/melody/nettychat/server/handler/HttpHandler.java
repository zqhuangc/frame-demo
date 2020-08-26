package com.melody.nettychat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;


/**
 * @author zqhuangc
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger LOG = Logger.getLogger(HttpHandler.class);

    // 获取 class 路径
    private URL baseURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
    private final String webroot = "webroot";


    private File getResource(String fileName) throws  Exception{
        String path = baseURL.toURI() + webroot + "/" + fileName;
        path = !path.contains("file:") ? path : path.substring(5);
        path = path.replaceAll("//", "/");
        return new File(path);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        String uri = request.uri();
        RandomAccessFile file = null;
        String page = uri.equals("/") ? "chat.html" : uri;

        try {
            file = new RandomAccessFile(getResource(page), "r");
        } catch (Exception e) {
            // 未找到文件，跳过
            ctx.fireChannelRead(request.retain());
            return;
        }

        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        String contentType = "text/html";
        if(uri.endsWith(".css")){
            contentType = "text/css";
        }else if(uri.endsWith(".js")){
            contentType = "text/javascript";
        }else if(uri.toLowerCase().matches("(jpg|png|gif)$")){
            String ext = uri.substring(uri.lastIndexOf("."));
            contentType = "image/" + ext + ";";
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType + ",charset=utf-8;");
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        // 长连接配置
        if(keepAlive){

            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());

            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);

        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));

        ctx.write(new ChunkedNioFile(file.getChannel()));

        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        if(!keepAlive){
            future.addListener(ChannelFutureListener.CLOSE);
        }

        file.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel client = ctx.channel();
        LOG.info("Client:"+client.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
