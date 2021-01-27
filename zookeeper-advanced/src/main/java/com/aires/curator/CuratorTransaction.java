package com.aires.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.framework.api.transaction.TypeAndPath;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.ZooDefs;
import org.checkerframework.checker.units.qual.C;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/27 15:44
 */
@Slf4j(topic = "c.CuratorTransaction")
public class CuratorTransaction {
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
     * 事务
     */
    @Test
    public void tran() throws Exception {
       /* try {
            client.inTransaction()
                    .create().forPath("/tran1", "tran1".getBytes(StandardCharsets.UTF_8))
                    .and()
                    .setData().forPath("/node2", "node2".getBytes(StandardCharsets.UTF_8))
                    .and().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        System.out.println("=====================================");


        // 新版事务
        List<CuratorTransactionResult> results = client.transaction().forOperations(client.transactionOp().create()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath("/aires", "aires".getBytes(StandardCharsets.UTF_8)), client.transactionOp().setData()
                .withVersion(-1)
                .forPath("/zed", "zed".getBytes(StandardCharsets.UTF_8)));

        for (CuratorTransactionResult result : results) {
            System.out.println(result.getType());
            System.out.println(result.getForPath());
            System.out.println(result.getResultPath());
            System.out.println(result.getResultStat());
        }
    }


}
