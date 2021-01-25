package com.aires.order02.zookeeper.controller;

import com.aires.order02.zookeeper.lock.ZookeeperLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class OrderController {


    @Value("${server.port}")
    private String port;

    private final StringRedisTemplate redisTemplate;


    @GetMapping("/zookeeper_distributed_lock")
    public String deductGoods() throws IOException, InterruptedException {
        ZookeeperLock lock = new ZookeeperLock();
        String result = redisTemplate.opsForValue().get("goods:001");
        int goodsNum = StringUtils.isEmpty(result) ? 0 : Integer.parseInt(result);
        if (goodsNum <= 0) {
            log.info("商品库存不足,购买失败! 服务端口号:{}", port);
            lock.unLock();
            return String.format("商品库存不足,购买失败! 服务端口号:%s", port);
        }
        lock.lock();
        int realNum = goodsNum - 1;
        redisTemplate.opsForValue().set("goods:001", String.valueOf(realNum));
        log.info("成功买到商品,库存还剩余:{}件  服务端口:{}", realNum, port);
        lock.unLock();
        return String.format("成功买到商品,库存还剩余:%s件  服务端口:%s", realNum, port);
    }
}
