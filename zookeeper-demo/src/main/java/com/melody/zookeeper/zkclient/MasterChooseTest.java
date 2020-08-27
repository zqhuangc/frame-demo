package com.melody.zookeeper.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * 测试 选举
 * @author zqhuangc
 */
public class MasterChooseTest {

    private final static String CONNECT_STRING ="192.168.11.129:2181,192.168.11.134:2181," +
            "192.168.11.135:2181,192.168.11.136:2181";


    public static void main(String[] args) {
        List<ZkClientMasterSelector> selectorLists=new ArrayList<>();
        try {
            for(int i = 0; i < 10; i++) {
                ZkClient zkClient = new ZkClient(CONNECT_STRING, 5000,
                        5000,
                        new SerializableSerializer());
                UserCenter userCenter = new UserCenter();
                userCenter.setMc_id(i);
                userCenter.setMc_name("客户端：" + i);

                ZkClientMasterSelector selector = new ZkClientMasterSelector(userCenter,zkClient);
                selectorLists.add(selector);
                selector.start();//触发选举操作
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for(ZkClientMasterSelector selector:selectorLists){
                selector.stop();
            }
        }
    }
}
