package com.melody.nettychat.protocol;

/**
 * 自定义IM协议，Instant Messaging Protocol即时通信协议
 * @author zqhuangc
 */
public enum IMProtocol {
    /** 系统信息 */
    SYSTEM("SYSTEM"),
    /** 登录命令 */
    LOGIN("LOGIN"),
    /** 登出命令 */
    LOGOUT("LOGOUT"),
    /** 聊天信息 */
    CHAT("CHAT"),
    /** 送鲜花 */
    FLOWER("FLOWER");

    private String name;

    public static boolean isIMP(String content){
        return content.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT)\\]");
    }

    IMProtocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
