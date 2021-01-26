package com.aires.order01.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/26 9:47
 */
@Slf4j(topic = "c.MyLock")
@Service
public class MyLock {
    private final String ROOT_PATH = "/Locks";

    private final String NODE_NAME = "lock";

    private String nodePath;

    private final ZooKeeper zooKeeper;


    public MyLock() throws IOException, InterruptedException {
        zooKeeper = ZookeeperClient.getInstance();
    }


    public void getLock() throws KeeperException, InterruptedException {
        createLock();
        tryLock();
    }

    public void createLock() throws KeeperException, InterruptedException {
        // 判断/Locks节点是否存在
        Stat stat = zooKeeper.exists(ROOT_PATH, false);
        if (stat == null) {
            zooKeeper.create(ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 创建临时有序节点
        nodePath = zooKeeper.create(ROOT_PATH + "/" + NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public void tryLock() throws KeeperException, InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition condition = reentrantLock.newCondition();
        List<String> children = zooKeeper.getChildren(ROOT_PATH, false);
        Collections.sort(children);
        int index = children.indexOf(nodePath.substring(ROOT_PATH.length() + 1));
        if (index == 0) {
            return;
        } else {
            // 上一个节点的路径
            String path = children.get(index - 1);
            Stat exists = zooKeeper.exists(ROOT_PATH + "/" + path, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                        synchronized (MyLock.class) {
                            condition.signalAll();
                        }
                    }
                }
            });
            if (exists == null) {
                tryLock();
            } else {
                synchronized (MyLock.class) {
                    condition.await();
                }
                tryLock();
            }
        }
    }

    public void releaseLock() throws KeeperException, InterruptedException {
        zooKeeper.delete(this.nodePath, -1);
        zooKeeper.close();
    }

}
