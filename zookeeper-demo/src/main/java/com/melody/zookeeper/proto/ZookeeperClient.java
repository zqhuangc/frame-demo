package com.melody.zookeeper.proto;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 提供 zookeeper 客户端连接实例
 * @author zqhuangc
 */
public class ZookeeperClient {
    private final static String CONNECT_STRING ="192.168.11.129:2181,192.168.11.134:2181," +
            "192.168.11.135:2181,192.168.11.136:2181";

    private static int sessionTimeout = 5000;

    //获取连接
    public static ZooKeeper getInstance() throws IOException, InterruptedException {
        final CountDownLatch connectStatus = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(CONNECT_STRING, sessionTimeout, event -> {
                if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    connectStatus.countDown();
                }
        });
        connectStatus.await();
        return zooKeeper;
    }

    public static int getSessionTimeout() {
        return sessionTimeout;
    }
}
