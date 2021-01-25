package com.aires.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/25 15:52
 */
@Slf4j(topic = "c.ZookeeperLock")
public class ZookeeperLock {

    private static final String LOCK_ROOT_PATH = "/Locks";

    private static final String LOCK_NODE_NAME = "Lock_";

    private String lockPath = "";

    private volatile static ZookeeperLock zookeeperLock;

    private static volatile ZooKeeper zooKeeper;

    public static ZookeeperLock getZookeeperLock() {
        if (zookeeperLock == null) {
            synchronized (ZookeeperLock.class) {
                if (zooKeeper == null) {
                    zookeeperLock = new ZookeeperLock();
                }
            }
        }
        return zookeeperLock;
    }


    public ZookeeperLock() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zooKeeper = new ZooKeeper("192.168.17.131" + ":" + "2181", 5000, watchedEvent -> {
                if (watchedEvent.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
                    log.info("链接zookeeper成功...");
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acquireLock() {
        createLock();
        tryAcquireLock();
    }

    public void createLock() {
        try {
            // 首先判断跟节点是否存在
            Stat exists = zooKeeper.exists(LOCK_ROOT_PATH, false);
            if (exists == null) {
                zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            // 创建临时有序节点(/Locks/lock_00000003)
            lockPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("创建临时-有序节点成功:{}", lockPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Watcher watcher = watchedEvent -> {
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
            log.info("前一个节点释放了锁........");
        }
    };


    public Boolean tryAcquireLock() {
        try {
            // 获取根节点下的所有临时有序节点
            List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH, false);
            Collections.sort(list);
            int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length() + 1));
            log.info("index...{}", index);
            // 根据索引判断是否获取到了锁
            if (index == 0) {
                log.info("获取到zk锁...{}", Thread.currentThread().getName());
                return true;
            } else {
                // 不是第一个临时有序节点,就只对前一个节点进行监控
                String preNodePath = list.get(index - 1);

                Stat stat = zooKeeper.exists(LOCK_ROOT_PATH + "/" + preNodePath, watcher);

                if (stat == null) {
                    tryAcquireLock();
                } else {
                    tryAcquireLock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void releaseLock() {
        try {
            zooKeeper.delete(lockPath, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("释放zk锁...{}", lockPath);
    }
}
