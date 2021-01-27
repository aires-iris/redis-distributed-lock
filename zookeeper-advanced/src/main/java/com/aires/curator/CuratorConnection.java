package com.aires.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;

import java.util.Random;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/27 10:32
 */
@Slf4j(topic = "c.CuratorConnection")
public class CuratorConnection {
    public static void main(String[] args) {



        // 重试策略
        /**        间隔时间= Math.max(1,new Random().nextInt(1<<(retryCount +1)))
         * ExponentialBackoffRetry
         *      RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);随着重连次数增加,每次间隔也会增加
         *
         * RetryUntilElapsed
         *      RetryPolicy policy = new RetryUntilElapsed(10000,3000);每3秒重试一次,总计超时时间不能大于10秒
         * RetryNTimes
         *      RetryPolicy policy = new RetryOneTime(3000);超时3秒后重试一次,并且只重试一次
         *      RetryPolicy policy = new RetryNTimes(3,3000);每3秒超时后重试,重试3次
         * RetryForever
         */
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .namespace("create")
                .sessionTimeoutMs(5000)
                .defaultData(new byte[0])
                .connectString("192.168.17.131:2181")
                .retryPolicy(policy)
                .build();
        client.start();
        System.out.println("链接对象...{}" + client);


        client.close();
    }
}
