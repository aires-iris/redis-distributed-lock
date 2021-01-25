package com.aires.order02.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/21 10:54
 */
@Slf4j(topic = "c.RedisConfig")
@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String, Serializable> getRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public Redisson getRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.17.131:6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }

}
