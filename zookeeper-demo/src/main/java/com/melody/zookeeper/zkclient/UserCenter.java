package com.melody.zookeeper.zkclient;

import java.io.Serializable;

/**
 *
 * @author zqhuangc
 */
public class UserCenter implements Serializable {
    private static final long serialVersionUID = 2999962619119201457L;

    private int mc_id; //机器信息
    private String mc_name;//机器名称

    public int getMc_id() {
        return mc_id;
    }

    public void setMc_id(int mc_id) {
        this.mc_id = mc_id;
    }

    public String getMc_name() {
        return mc_name;
    }

    public void setMc_name(String mc_name) {
        this.mc_name = mc_name;
    }

    @Override
    public String toString() {
        return "UserCenter{" +
                "mc_id=" + mc_id +
                ", mc_name='" + mc_name + '\'' +
                '}';
    }
}
