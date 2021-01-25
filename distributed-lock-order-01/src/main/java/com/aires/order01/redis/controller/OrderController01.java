package com.aires.order01.redis.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/21 10:58
 */
@Slf4j(topic = "c.OrderController")
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class OrderController01 {

    private final StringRedisTemplate redisTemplate;

    private final String REDIS_LOCK = "AIRES_LOCK";
    @Value("${server.port}")
    private String port;

    @GetMapping("/buy_goods")
    public String deductGoods() {

        String uuid = (UUID.randomUUID() + Thread.currentThread().getName()).replaceAll("-", "");
        Boolean izHoldLock = redisTemplate.opsForValue().setIfAbsent(REDIS_LOCK, uuid);

        // redis的setnx成功才能执行业务代码
        if (!izHoldLock) {
            return "抢锁失败";
        }
        String result = redisTemplate.opsForValue().get("goods:001");
        int goodsNum = StringUtils.isEmpty(result) ? 0 : Integer.parseInt(result);
        if (goodsNum > 0) {
            int realNum = goodsNum - 1;
            redisTemplate.opsForValue().set("goods:001", String.valueOf(realNum));
            log.info("成功买到商品,库存还剩余:{}件  服务端口:{}", realNum, port);
            // 解锁
            redisTemplate.delete(REDIS_LOCK);
            return String.format("成功买到商品,库存还剩余:%s件  服务端口:%s", realNum, port);
        } else {
            log.info("商品库存不足,购买失败! 服务端口号:{}", port);
            return String.format("商品库存不足,购买失败! 服务端口号:%s", port);
        }
    }
}
