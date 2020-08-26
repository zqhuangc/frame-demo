package com.melody.nettyserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.apache.log4j.Logger;

/**
 * 服务端
 * @author zqhuangc
 */
public class JerryMic {

    private static Logger LOG = Logger.getLogger(JerryMic.class);

    public void start(int port) throws Exception{
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap s = new ServerBootstrap();
            s.group(masterGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();
                            //服务端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            pipeline.addLast(new HttpResponseEncoder());
                            //服务端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            pipeline.addLast(new HttpRequestDecoder());
                            // 自定义处理
                            pipeline.addLast(new JerryMicHandler());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //绑定服务端口
            ChannelFuture f = s.bind(port).sync();

            LOG.info("HTTP服务已启动，监听端口:" + port);

            //开始接收客户
            f.channel().closeFuture().sync();
        }  finally {
            masterGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new JerryMic().start(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
