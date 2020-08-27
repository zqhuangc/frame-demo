package com.melody.zookeeper.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ZkClient 的使用
 * @author zqhuangc
 */
public class ZkClientApiDemo {

    private static final String CONNECT_STRING = "0.0.0.0:2181";

    public static void main(String[] args) throws Exception{

        ZkClient zkClient=new ZkClient(CONNECT_STRING,4000);

        //zkclient createPersistent 提供递归创建父节点的功能，临时节点
        zkClient.createPersistent("/zkclient/zkclient1/zkclient1-1/zkclient1-1-1",true);
        System.out.println("success");

        //删除节点
        zkClient.deleteRecursive("/zkclient");

        zkClient.createPersistent("/node/node1/node11",true);

        //获取子节点
        List<String> list = zkClient.getChildren("/node");
        System.out.println(list);

        //watcher
        zkClient.subscribeDataChanges("/node", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("节点名称："+s+"->节点修改后的值"+o);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("节点值删除："+ s);
            }
        });
        // 设置值
        zkClient.writeData("/node","node");
        TimeUnit.SECONDS.sleep(2);

        zkClient.subscribeChildChanges("/node", new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                List<String> strings = zkClient.watchForChilds(s);
                list.forEach(System.out::println);
                strings.forEach(System.out::println);
            }
        });
        System.in.read();
    }
}
