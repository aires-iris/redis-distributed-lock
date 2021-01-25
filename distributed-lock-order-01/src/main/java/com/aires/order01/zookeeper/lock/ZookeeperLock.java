package com.aires.order01.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2020-01-25 22:49
 */
@Slf4j(topic = "ZookeeperLock")
public class ZookeeperLock {
    private final String ROOT_PATH = "/lock";

    private ZooKeeper zooKeeper;

    private int sessionTimeOut;

    private String lockId;


    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZookeeperLock() throws IOException, InterruptedException {
        zooKeeper = ZookeeperClient.getInstance();
        sessionTimeOut = ZookeeperClient.SESSION_TIMEOUT;
    }

    public boolean lock() {
        try {
            // TODO 判断是否有根节点,如果没有需要首先创建
            // 创建临时有序节点

            lockId = zooKeeper.create(ROOT_PATH + "/" + "aires", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//            log.info("{}:创建了zk节点:{}", Thread.currentThread().getName(), lockId);
            // 获取全部的子节点
            List<String> children = zooKeeper.getChildren(ROOT_PATH, true);
            TreeSet<String> sortedSet = new TreeSet<>();
            if (!CollectionUtils.isEmpty(children)) {
                children.parallelStream().forEach(e -> sortedSet.add(ROOT_PATH + "/" + e));
            }

            // 第一个节点
            String first = sortedSet.first();

            if (StringUtils.isEmpty(first) || lockId.equals(first)) {
//                log.info("{}:获取zk锁成功...", Thread.currentThread().getName());
                return true;
            }
            // 获取当前节点的前节点
            SortedSet<String> frontSet = sortedSet.headSet(lockId);

            if (!CollectionUtils.isEmpty(frontSet)) {
                String last = frontSet.last();
//                log.info("{}:节点监听{}节点", lockId, last);
                // 当前节点去监听上一个节点的删除事件
                zooKeeper.exists(last, watchedEvent -> {
                    if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.await();
//                log.info("{}:尝试获取锁", lockId);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unLock() {
        try {
//            log.info("{}:开始删除锁:{}", Thread.currentThread().getName(), lockId);
            if (!StringUtils.isEmpty(lockId)) {
                zooKeeper.delete(lockId, -1);
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }
}
