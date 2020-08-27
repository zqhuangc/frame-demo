package com.melody.zookeeper.proto;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ZooKeeper 原生 API 的使用
 * @author zqhuangc
 */
public class ZookeeperAPI {

    private static final String CONNECT_STRING = "0.0.0.0:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zooKeeper;

    private static Stat stat = new Stat();

    public static void main(String[] args){
        //ZookeeperAPI.testApi();
        ZookeeperAPI.testSession();
    }

    public static void testSession() {
        try {
            zooKeeper = new ZooKeeper(CONNECT_STRING, 5000, watchedEvent -> {
                //如果当前的连接状态是连接成功的，那么通过计数器去控制
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                    System.out.println(watchedEvent.getState());
                }
            });
            System.out.println(zooKeeper.getState());
            countDownLatch.await();
            System.out.println(zooKeeper.getState());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testApi() throws Exception{
        zooKeeper= new ZooKeeper(CONNECT_STRING, 5000, event -> {});
        countDownLatch.await();

/*
        ACL acl=new ACL(ZooDefs.Perms.ALL,new Id("ip","192.168.11.129"));
        List<ACL> acls=new ArrayList<>();
        acls.add(acl);
        zooKeeper.create("/authTest","111".getBytes(),acls,CreateMode.PERSISTENT);
        zooKeeper.getData("/authTest",true,new Stat());
*/
        System.out.println(zooKeeper.getState());

        //创建节点
        String result = zooKeeper.create("/test1", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        //增加一个
        zooKeeper.getData("/test1", new WatcherApi(), stat);
        System.out.println("创建成功："+result);

        //修改数据
        zooKeeper.setData("/test1","testUpdate".getBytes(),-1);
        Thread.sleep(2000);
        byte[] data = zooKeeper.getData("/test1", new WatcherApi(), stat);
        System.out.println("数据为："+ new String(data));

        //修改数据
        zooKeeper.setData("/test1","testUpdate2".getBytes(),-1);
        Thread.sleep(2000);

        //删除节点
        zooKeeper.delete("/test1",-1);
        Thread.sleep(2000);

        //创建节点和子节点
        String path="/test11";

        zooKeeper.create(path,"123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        TimeUnit.SECONDS.sleep(1);

        Stat stat= zooKeeper.exists(path+"/test",true);
        if(stat == null){//表示节点不存在
            zooKeeper.create(path+"/test","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            TimeUnit.SECONDS.sleep(1);
        }
        //修改子路径
        zooKeeper.setData(path+"/test","test123".getBytes(),-1);
        TimeUnit.SECONDS.sleep(1);
        //获取指定节点下的子节点
        List<String> childrens = zooKeeper.getChildren("/test",true);
        System.out.println(childrens);
    }


    public static void testAuth() throws Exception{
        zooKeeper = new ZooKeeper(CONNECT_STRING, 5000, new WatcherApi());
        countDownLatch.await();

        ACL acl = new ACL(ZooDefs.Perms.CREATE, new Id("digest","root:root"));
        ACL acl2 = new ACL(ZooDefs.Perms.CREATE, new Id("ip","192.168.1.1"));

        List<ACL> acls = new ArrayList<>();
        acls.add(acl);
        acls.add(acl2);
        zooKeeper.create("/auth1","123".getBytes(),acls,CreateMode.PERSISTENT);
        zooKeeper.addAuthInfo("digest","root:root".getBytes());
        zooKeeper.create("/auth1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        zooKeeper.create("/auth1/auth1-1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.EPHEMERAL);


        ZooKeeper zooKeeper1 = new ZooKeeper(CONNECT_STRING, 5000, new WatcherApi());
        countDownLatch.await();
        zooKeeper1.delete("/auth1",-1);

        // acl (create /delete /admin /read/write)
        //权限模式： ip/Digest（username:password）/world/super

    }




    static class WatcherApi implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            //如果当前的连接状态是连接成功的，那么通过计数器去控制
            if(event.getState() == Event.KeeperState.SyncConnected){
                if(Event.EventType.None == event.getType()){
                    countDownLatch.countDown();
                    System.out.println(event.getState() + "-->" + event.getType());
                } else if(event.getType() == Event.EventType.NodeDataChanged){
                    try {
                        System.out.println("数据变更触发路径："+ event.getPath()+"->改变后的值：" +
                                zooKeeper.getData(event.getPath(),true, stat));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if(event.getType() == Event.EventType.NodeChildrenChanged){
                    //子节点的数据变化会触发
                    try {
                        System.out.println("子节点数据变更路径："+ event.getPath()+"->节点的值："+
                                zooKeeper.getData(event.getPath(),true,stat));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if(event.getType() == Event.EventType.NodeCreated){
                    //创建子节点的时候会触发
                    try {
                        System.out.println("节点创建路径："+event.getPath()+"->节点的值："+
                                zooKeeper.getData(event.getPath(),true,stat));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else if(event.getType()== Event.EventType.NodeDeleted){
                    //子节点删除会触发
                    System.out.println("节点删除路径："+ event.getPath());
                }
                System.out.println(event.getType());
            }
        }
    }

}



