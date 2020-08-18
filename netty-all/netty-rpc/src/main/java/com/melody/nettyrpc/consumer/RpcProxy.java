package com.melody.nettyrpc.consumer;

import com.melody.nettyrpc.base.CallMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zqhuangc
 */
public class RpcProxy {

    public static <T> T create(Class<?> clazz){
        MethodProxy proxy = new MethodProxy(clazz);
        return  (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, proxy);
    }
}

class MethodProxy implements InvocationHandler{

    private Class<?> clazz;

    public MethodProxy(Class<?> clazz) {
        this.clazz = clazz;
    }

    //代理，调用IRpcXXX接口中每一个方法的时候，实际上就是发起一次网络请求
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this, args);
        }else {
            return rpcInvoke(method, args);
        }


    }

    public Object rpcInvoke(Method method, Object[] args){

        CallMsg msg = new CallMsg();
        msg.setClassName(this.clazz.getName());
        msg.setMethodName(method.getName());
        msg.setParams(method.getParameterTypes());
        msg.setValues(args);

        EventLoopGroup group = new NioEventLoopGroup();

        final RpcProxyHandler handler = new RpcProxyHandler();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();


                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

                            //处理序列化的解、编码器（JDK默认的序列化）
                            pipeline.addLast("encoder",new ObjectEncoder());
                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            //自己的业务逻辑
                            pipeline.addLast("handler", handler);
                        }
                    });
            ChannelFuture f = bootstrap.connect("localhost", 8080).sync();
            f.channel().writeAndFlush(msg).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

        return handler.getResult();
    }
}
