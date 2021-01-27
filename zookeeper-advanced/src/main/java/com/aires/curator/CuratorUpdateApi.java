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

import java.nio.charset.StandardCharsets;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/27 11:23
 */
@Slf4j(topic = "c.CuratorSetApi")
public class CuratorUpdateApi {
    private CuratorFramework client;

    @Before
    public void before() {

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.17.131:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(policy)
                .defaultData(new byte[0])
                .namespace("aires")
                .build();
        client.start();
    }

    @After
    public void after() {

        client.close();
    }

    @Test
    public void update1() throws Exception {
        client.setData().forPath("/node1", "11".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 指定版本号更新(类似CAS)
     *
     * @throws Exception
     */
    @Test
    public void update2() throws Exception {
        client.setData().withVersion(2)
                .forPath("/node2", "22".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 异步方式修改
     *
     * @throws Exception
     */
    @Test
    public void update3() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                client.setData().withVersion(-1)
                        .inBackground((curatorFramework, curatorEvent) -> {
                            System.out.println(curatorEvent.getType());
                            System.out.println(curatorEvent.getPath());
                        })
                        .forPath("/node3","333".getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join();
    }
}
