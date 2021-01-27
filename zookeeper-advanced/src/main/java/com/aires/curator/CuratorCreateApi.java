package com.aires.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.checkerframework.checker.units.qual.A;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/27 10:55
 */
@Slf4j(topic = "c.CuratorApi")
public class CuratorCreateApi {

    private CuratorFramework client;

    @Before
    public void before() {

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.17.131:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(policy)
                .defaultData(new byte[0])
                .namespace("create")
                .build();
        client.start();
    }

    @After
    public void after() {

        client.close();
    }


    @Test
    public void create1() throws Exception {
        String path = client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath("/node1", "node1".getBytes(StandardCharsets.UTF_8));
        System.out.println("创建完成:"+path);
    }

    /**
     * 自定义节点ACL
     * @throws Exception
     */
    @Test
    public void create2() throws Exception {

        ArrayList<ACL> list = new ArrayList<>();
        list.add(new ACL(ZooDefs.Perms.ALL,new Id("ip","192.168.17.131")));

        String path = client.create()
                .creatingParentsIfNeeded()// 递归创建节点
                .withMode(CreateMode.PERSISTENT)
                .withACL(list)
                .forPath("/node3", "node3".getBytes(StandardCharsets.UTF_8));
        System.out.println("创建完成:"+path);
    }

    /**
     * 异步创建节点
     */
    @Test
    public void create3() throws Exception {
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println("curator:"+curatorFramework.toString());
                    System.out.println("事件:"+curatorEvent.toString());
                }).forPath("/node4","node4".getBytes(StandardCharsets.UTF_8));
        Thread.sleep(5000);
    }
}
