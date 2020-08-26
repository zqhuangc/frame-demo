package com.melody.nettychat.client.handler;

import com.melody.nettychat.protocol.IMMessage;
import com.melody.nettychat.protocol.IMProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * @author zqhuangc
 */
public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger LOG = Logger.getLogger(ChatClientHandler.class);
    private ChannelHandlerContext ctx;
    private String nickName;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 启动客户端控制台
     * @throws Exception
     */
    private void session() throws Exception{
        new Thread(() -> {
            LOG.info(nickName + ",你好，请在控制台输入消息内容");
            IMMessage message = null;
            Scanner scanner = null;
            scanner = new Scanner(System.in);
            do{
                if(scanner.hasNext()){
                    String input = scanner.nextLine();
                    if("exit".equals(input)){
                        message = new IMMessage(IMProtocol.LOGOUT.getName(), System.currentTimeMillis(), nickName);
                    }else{
                        message = new IMMessage(IMProtocol.CHAT.getName(),System.currentTimeMillis(), nickName, input);
                    }
                }
            }while(sendMsg(message));
            scanner.close();
        }).start();
    }

    /**
     * tcp链路建立成功后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        IMMessage message = new IMMessage(IMProtocol.LOGIN.getName(), System.currentTimeMillis(), this.nickName);
        sendMsg(message);
        LOG.info("成功连接服务器,已执行登录动作");
        session();
    }

    /**
     * 发送信息
     * @param msg
     * @return
     */
    private boolean sendMsg(IMMessage msg){
        ctx.channel().writeAndFlush(msg);
        LOG.info("已发送至聊天面板,请继续输入");
        return !msg.getCmd().equals(IMProtocol.LOGOUT.getName());
    }

    /**
     * 收到信息后调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMMessage m = (IMMessage) msg;
        LOG.info(m);
    }


    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.info("与服务器断开连接:"+cause.getMessage());
        ctx.close();
    }
}
