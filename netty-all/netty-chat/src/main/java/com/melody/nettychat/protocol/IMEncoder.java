package com.melody.nettychat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;


/**
 * 自定义 IM 协议编码器
 * @author zqhuangc
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(new MessagePack().write(msg));
    }

    public String encode(IMMessage msg){
        if(null == msg){
            return "";
        }
        String pre = "[" + msg.getCmd() + "]" + "[" +msg.getTime() + "]";

        if(IMProtocol.LOGIN.getName().equals(msg.getCmd()) ||
                IMProtocol.CHAT.getName().equals(msg.getCmd()) ||
                IMProtocol.FLOWER.getName().equals(msg.getCmd()) ){
            pre += ("[" + msg.getSender() + "]");
        }else if (IMProtocol.SYSTEM.getName().equals(msg.getCmd())){
            pre += ("[" + msg.getOnline() + "]");
        }

        if(!(null == msg.getContent() || "".equals(msg.getContent()))){
            pre += (" - " + msg.getContent());
        }

        return pre;
    }
}
