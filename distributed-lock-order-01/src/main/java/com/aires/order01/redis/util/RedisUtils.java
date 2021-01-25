package com.aires.order01.redis.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/21 14:55
 */
@Slf4j(topic = "c.RedisUtils")
public class RedisUtils {

    private static JedisPool jedisPool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        jedisPool = new JedisPool(config, "192.168.248.135", 6379);
    }

    public static Jedis getJedis() {
        if (jedisPool != null) {
            return jedisPool.getResource();
        }else {
            throw new RuntimeException("Jedis Pool is not ok!");
        }

    }
}


