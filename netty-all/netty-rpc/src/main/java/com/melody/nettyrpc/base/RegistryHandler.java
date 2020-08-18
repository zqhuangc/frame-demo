package com.melody.nettyrpc.base;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 接收处理
 * 处理注册中心的业务逻辑
 * @author zqhuangc
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    // 注册服务存放容器
    public static ConcurrentMap<String, Object> registryMap =  new ConcurrentHashMap<>();

    private List<String> serviceList = new ArrayList<>();

    public RegistryHandler() {
        scanClass("com.melody.nettyrpc.provider");
        doRegister();
    }

    // 类IOC容器，扫描加载类
    private void scanClass(String path){
        URL url = this.getClass().getClassLoader().getResource(path.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file: dir.listFiles()) {
            if(file == null){
                continue;
            }
            if(file.isDirectory()){
                scanClass(path + "." + file.getName());
            } else {
                serviceList.add(path + "." +file.getName().replace(".class","").trim());
            }

        }
    }

    //把扫描到的 class 实例化，放到map中，注册服务
    //注册的服务名应为接口
    //约定优于配置
    private void doRegister(){
        if(serviceList.size() == 0){
            return;
        }

        for (String serviceClass : serviceList) {
            try {
                Class<?> clazz = Class.forName(serviceClass);
                Class<?> interfaces = clazz.getInterfaces()[0];
                registryMap.put(interfaces.getName(), clazz.getDeclaredConstructor().newInstance());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Object result = new Object();
        // 客户端请求调用信息
        CallMsg request = (CallMsg) msg;
        if(registryMap.containsKey(request.getClassName())){
            Object clazz = registryMap.get(request.getClassName());

            Method m = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = m.invoke(clazz, request.getValues());

        }

        ctx.writeAndFlush(result);
        ctx.close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
