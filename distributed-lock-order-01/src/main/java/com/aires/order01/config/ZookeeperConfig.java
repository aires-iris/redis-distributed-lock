package com.aires.order01.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/25 12:00
 */
@Slf4j(topic = "c.ZookeeperConfig")
//@Configuration
public class ZookeeperConfig {

    @Value("${vmware.server.ip}")
    private String ip;

    @Value("${vmware.server.port}")
    private String port;

    /**
     * zookeeper链接的配置初始化
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Bean
    public ZooKeeper getZooKeeper() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        ZooKeeper zooKeeper = new ZooKeeper(ip + ":" + port, 5000, watchedEvent -> {
            if (watchedEvent.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
                log.info("链接zookeeper成功...");
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }
}
