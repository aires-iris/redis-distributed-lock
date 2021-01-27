package com.aires.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/27 11:42
 */
@Slf4j(topic = "c.CuratorDeleteApi")
public class CuratorDeleteApi {
    private CuratorFramework client;

    @Before
    public void before() {

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.17.131:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(policy)
                .defaultData(new byte[0])
                .namespace("del")
                .build();
        client.start();
    }

    @After
    public void after() {

        client.close();
    }

    @Test
    public void delete1() throws Exception {
        client.delete().withVersion(-1).forPath("/node1");
    }

    /**
     * 异步删除节点,带版本号
     * @throws Exception
     */
    @Test
    public void delete2() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                client.delete().withVersion(-1).inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println(curatorEvent.getType());
                    System.out.println(curatorEvent.getPath());
                }).forPath("/node2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join();
    }

    /**
     * 级联删除节点
     */
    @Test
    public void delete3() throws Exception {
        client.delete().deletingChildrenIfNeeded().withVersion(-1).forPath("/node4");
    }
}
