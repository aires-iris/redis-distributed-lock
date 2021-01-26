package com.aires.order01.zookeeper.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/26 10:10
 */
@Slf4j(topic = "c.OrderServiceImpl")
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class OrderServiceImpl implements OrderService {

    @Value("${server.port}")
    private String port;


    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public String sell() {
        String result = redisTemplate.opsForValue().get("goods:001");
        int goodsNum = StringUtils.isEmpty(result) ? 0 : Integer.parseInt(result);
        if (goodsNum <= 0) {
            log.info("商品库存不足,购买失败! 服务端口号:{}", port);
            return String.format("商品库存不足,购买失败! 服务端口号:%s", port);
        }
        int realNum = goodsNum - 1;
        redisTemplate.opsForValue().set("goods:001", String.valueOf(realNum));
        log.info("成功买到商品,库存还剩余:{}件  服务端口:{}", realNum, port);
        return String.format("成功买到商品,库存还剩余:%s件  服务端口:%s", realNum, port);

    }
}
