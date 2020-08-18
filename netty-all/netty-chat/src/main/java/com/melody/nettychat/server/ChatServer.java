package com.melody.nettychat.server;

import com.melody.nettychat.protocol.IMDecoder;
import com.melody.nettychat.protocol.IMEncoder;
import com.melody.nettychat.server.handler.HttpHandler;
import com.melody.nettychat.server.handler.SocketHandler;
import com.melody.nettychat.server.handler.WebSocketHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

/**
 * @author zqhuangc
 */
public class ChatServer {

    private static Logger LOG = Logger.getLogger(ChatServer.class);

    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start(){

        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap s = new ServerBootstrap();
            s.group(masterGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            // 解析自定义协议
                            pipeline.addLast(new IMDecoder());
                            pipeline.addLast(new IMEncoder());
                            pipeline.addLast(new SocketHandler());

                            /** 解析 HTTP 请求 */
                            pipeline.addLast(new HttpServerCodec());
                            //主要是将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            //主要用于处理大数据流,比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的 ,使用该 handler我们就不用考虑这个问题了
                            pipeline.addLast(new ChunkedWriteHandler());
                            // 自定义处理
                            pipeline.addLast(new HttpHandler());

                            /** 解析 webSocket 请求 */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                            // 自定义处理
                            pipeline.addLast(new WebSocketHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = s.bind(this.port).sync();
            LOG.info("服务已启动,监听端口" + this.port);
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            masterGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer(8080).start();
    }
}
