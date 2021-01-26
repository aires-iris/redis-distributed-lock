package com.aires.order01.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2020-01-25 22:45
 */
@Slf4j(topic = "ZookeeperClient")
public class ZookeeperClient {
    public static final String ZOOKEEPER_CONNECT = "192.168.17.131:2181";

    public static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static final int SESSION_TIMEOUT = 50000;

    private volatile static ZooKeeper instance;

    /**
     * 单例模式获取zk连接对象
     *
     * @return
     * @throws IOException
     */
    public static ZooKeeper getInstance() throws IOException, InterruptedException {
        if (instance == null) {
            synchronized (ZookeeperClient.class) {
                if (instance == null) {
                    instance = new ZooKeeper(ZOOKEEPER_CONNECT, SESSION_TIMEOUT, watchedEvent -> {
                        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
//                            log.info("连接zookeeper成功.....");
                            countDownLatch.countDown();
                        }
                    });
                    countDownLatch.await();
                }
            }
        }
        return instance;
    }

    public static Integer getSessionTimeOut() {
        return SESSION_TIMEOUT;
    }
}
