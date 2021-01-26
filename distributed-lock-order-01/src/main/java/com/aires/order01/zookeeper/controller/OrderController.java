package com.aires.order01.zookeeper.controller;

import com.aires.order01.zookeeper.lock.MyLock;
import com.aires.order01.zookeeper.lock.OrderService;
import com.aires.order01.zookeeper.lock.ZookeeperLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/21 10:58
 */
@RestController("zookeeper")
@Slf4j(topic = "c.OrderController")

public class OrderController {


    @Autowired
    MyLock myLock;

    @Autowired
    private OrderService orderService;

    @GetMapping("/zookeeper_distributed_lock")
    public void deductGoods() throws IOException, InterruptedException, KeeperException {
        myLock.getLock();
        orderService.sell();
        myLock.releaseLock();
    }
}
