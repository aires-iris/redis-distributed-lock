package com.aires.order01.zookeeper.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/26 9:41
 */
@Slf4j(topic = "c.ZookeeperConfig")
@Configuration
public class ZookeeperConfig {

    @Bean
    public ZooKeeper getZookeeper() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("192.168.17.131:2181", 5000, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                log.info("连接zookeeper成功.....");
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }

}
