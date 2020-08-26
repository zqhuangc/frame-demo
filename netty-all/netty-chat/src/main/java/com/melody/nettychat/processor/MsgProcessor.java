package com.melody.nettychat.processor;

import com.alibaba.fastjson.JSONObject;
import com.melody.nettychat.protocol.IMDecoder;
import com.melody.nettychat.protocol.IMEncoder;
import com.melody.nettychat.protocol.IMMessage;
import com.melody.nettychat.protocol.IMProtocol;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * 自定义协议内容的逻辑处理器
 * @author zqhuangc
 */
public class MsgProcessor {

    // 记录在线人数
    private static ChannelGroup onlineUsers =  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 定义一些扩展属性
    private final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    private final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    private final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");

    // 自定义解码器
    private IMDecoder decoder = new IMDecoder();
    // 自定义编码器
    private IMEncoder encoder = new IMEncoder();


    /**
     * 获取用户昵称
     * @param client
     * @return
     */
    public String getNickName(Channel client){
        return client.attr(NICK_NAME).get();

    }

    /**
     * 获取用户远程IP地址
     * @param client
     * @return
     */
    public String getAddress(Channel client){
        return client.remoteAddress().toString().replaceFirst("/","");
    }

    /**
     * 获取扩展属性
     * @param client
     * @return
     */
    public JSONObject getAttrs(Channel client){
        try {
            return client.attr(ATTRS).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置扩展属性
     * @param client
     * @param key
     * @param value
     */
    public void setAttrs(Channel client, String key, Object value){
        try {
            JSONObject json = client.attr(ATTRS).get();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        }
    }

    /**
     * 登出
     * @param client
     */
    public void logout(Channel client){
        if(getNickName(client) == null) { return; }
        for (Channel channel : onlineUsers) {
            IMMessage request = new IMMessage(IMProtocol.SYSTEM.getName(), sysTime(), onlineUsers.size(), getNickName(channel));
            String content = encoder.encode(request);
            channel.writeAndFlush(new TextWebSocketFrame(content));

        }
        onlineUsers.remove(this);
    }

    /**
     * 获取系统时间
     * @return
     */
    private Long sysTime(){
        return System.currentTimeMillis();
    }

    /**
     * 发送信息
     * @param client
     * @param msg
     */
    public void sendMsg(Channel client, IMMessage msg){
        sendMsg(client, encoder.encode(msg));
    }

    /**
     * 发送信息
     * @param client
     * @param msg
     */
    public void sendMsg(Channel client, String msg){
        IMMessage request = decoder.decode(msg);
        if(null == request){
            return;
        }

        String addr = getAddress(client);

        if(request.getCmd().equals(IMProtocol.LOGIN.getName())){
            client.attr(NICK_NAME).getAndSet(request.getSender());
            client.attr(IP_ADDR).getAndSet(addr);
            onlineUsers.add(client);

            for (Channel channel : onlineUsers) {
                IMMessage req = null;
                if(channel != client){
                    req = new IMMessage(IMProtocol.SYSTEM.getName(), sysTime(), onlineUsers.size(), getNickName(client) + "加入");
                }else{
                    req = new IMMessage(IMProtocol.SYSTEM.getName(), sysTime(), onlineUsers.size(), "已与服务器建立连接！");
                }
                String content = encoder.encode(req);
                channel.writeAndFlush(new TextWebSocketFrame(content));

            }
        }else if(request.getCmd().equals(IMProtocol.CHAT.getName())){
            for (Channel channel : onlineUsers) {
                if(channel == client){
                    request.setSender("you");
                }else {
                    request.setSender(getNickName(client));
                }
                request.setTime(sysTime());
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));

            }
        }else if(request.getCmd().equals(IMProtocol.FLOWER.getName())){
            JSONObject attrs = getAttrs(client);
            Long currTime = sysTime();
            if(null != attrs){
                long lastTime = attrs.getLongValue("lastFlowerTime");
                // 60秒之内不允许重复刷鲜花
                int seconds = 10;
                long sub = currTime - lastTime;
                if (sub < 1000 * seconds){
                    request.setSender("you");
                    request.setCmd(IMProtocol.SYSTEM.getName());
                    request.setContent("您送鲜花太过频繁，" + (seconds - Math.round(sub / 1000) + "秒后再试"));
                    String content = encoder.encode(request);
                    client.writeAndFlush(new TextWebSocketFrame(content));
                }
            }

            // 正常送花
            for (Channel channel : onlineUsers) {
                if(channel == client){
                    request.setSender("you");
                    request.setContent("你给大家送了一大波鲜花");
                    setAttrs(client, "lastFlowerTime", currTime);
                }else {
                    request.setSender(getNickName(client));
                    request.setContent(getNickName(client) + "送出一大波鲜花");
                }
                request.setTime(sysTime());

                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }
    }
}
