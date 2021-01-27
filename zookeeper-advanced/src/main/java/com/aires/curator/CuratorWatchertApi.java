package com.aires.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
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
public class CuratorWatchertApi {
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


    /**
     * 使用watcher监视节点变化
     * @throws Exception
     */
    @Test
    public void watcher1() throws Exception {
        // 监视某个节点的状态变化
        final NodeCache cache = new NodeCache(client, "/watcher1");
        cache.start();

        new Thread(() -> {
            cache.getListenable().addListener(() -> {
                System.out.println(cache.getPath());
                System.out.println(new String(cache.getCurrentData().getData()));
                System.out.println("发生状态改变");
            });
        }).start();

        Thread.sleep(100000);
        cache.close();
    }


    /**
     * 监视子节点的状态变化
     * @throws Exception
     */
    @Test
    public void watcher2() throws Exception {

        /**
         * 第三个参数在回调的时候是否可以读取节点数据
         */
        PathChildrenCache cache = new PathChildrenCache(client, "/watcher1", true);
        cache.start();
        Thread thread = new Thread(() -> {
            try {
               cache.getListenable().addListener((curatorFramework, pathChildrenCacheEvent) -> {
                   System.out.println(pathChildrenCacheEvent.getType());
                   System.out.println(pathChildrenCacheEvent.getData().toString());
               });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        thread.start();
        Thread.sleep(200000);
        cache.clear();

    }
}
