package com.aires.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/27 11:51
 */
@Slf4j(topic = "c.CuratorGetApi")
public class CuratorChildApi {
    private CuratorFramework client;

    @Before
    public void before() {

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.17.131:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(policy)
                .defaultData(new byte[0])
                .build();
        client.start();
    }

    @After
    public void after() {

        client.close();
    }

    @Test
    public void get1() throws Exception {
        List<String> list = client.getChildren().forPath("/node");
        list.forEach(System.out::println);
    }


    @Test
    public void get2() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                client.getChildren().inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println(curatorEvent.getPath());
                    System.out.println(curatorEvent.getType());
                    List<String> list = curatorEvent.getChildren();
                    list.forEach(System.out::println);
                }).forPath("/node");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();

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
