package com.melody.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.List;


/**
 * Curator 的使用
 * @author zqhuangc
 */
public class CuratorApiDemo {

    private static final String CONNECT_STRING = "0.0.0.0:2181";


    public static void testSession() throws Exception {

        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(CONNECT_STRING, 5000, 5000, retryPolicy);
        client.start();

        client.create().forPath("/node","test".getBytes());

        client.create().withMode(CreateMode.EPHEMERAL).forPath("/node1","test".getBytes());
        client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/node2/node","test".getBytes());

//        client.setData().forPath();
        client.getCuratorListenable().addListener((curatorFramework, curatorEvent) -> {

        });
        client.create().inBackground().forPath("/node11","test1".getBytes());

        client.delete().guaranteed();
        client.getChildren().watched();
        client.getChildren().usingWatcher((Watcher) watchedEvent -> {

        });
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

            }
        });


        CuratorOp curatorOp = client.transactionOp().create().forPath("");
        List<CuratorTransactionResult> curatorTransactionResults = client.transaction().forOperations(curatorOp);


    }

    /**
     * 选举
     */
    public static void testLeader(){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("").connectionTimeoutMs(1000).sessionTimeoutMs(1000).build();
        client.start();

        LeaderSelector leaderSelector = new LeaderSelector(client, "/node", new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {

            }
        });
        leaderSelector.autoRequeue();
        leaderSelector.start();
    }
}
