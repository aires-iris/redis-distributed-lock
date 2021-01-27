package com.aires.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/27 11:51
 */
@Slf4j(topic = "c.CuratorGetApi")
public class CuratorGetApi {
    private CuratorFramework client;

    @Before
    public void before() {

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.17.131:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(policy)
                .defaultData(new byte[0])
                .namespace("get")
                .build();
        client.start();
    }

    @After
    public void after() {

        client.close();
    }

    @Test
    public void get1() throws Exception {
        byte[] bytes = client.getData().forPath("/node1");
        System.out.println(new String(bytes));
    }


    /**
     * 读取节点数据时同时读取节点属性
     */
    @Test
    public void get2() throws Exception {
        Stat stat = new Stat();
        byte[] bytes = client.getData().storingStatIn(stat).forPath("/node2");
        System.out.println(new String(bytes));
        System.out.println(stat.getAversion());
        System.out.println(stat.getNumChildren());
    }


    /**
     * 异步方式获取节点数据
     *
     * @throws Exception
     */
    @Test
    public void get3() throws Exception {
        Thread thread = new Thread(() -> {
            byte[] bytes = new byte[0];
            try {
                client.getData().inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println(curatorEvent.toString());
                    System.out.println(new String(curatorEvent.getData()));
                }).forPath("/node3");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        thread.start();
        thread.join();

    }

}
