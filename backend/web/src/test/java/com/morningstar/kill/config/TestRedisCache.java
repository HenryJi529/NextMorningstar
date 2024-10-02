package com.morningstar.kill.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ZSetOperations;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRedisCache {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test1(){
        redisTemplate.opsForValue().set("name","henry");
        assert "henry".equals(redisTemplate.opsForValue().get("name"));
    }

    @Test
    public void test2(){
        HashOperations<String, String, Integer> hashOperations = redisTemplate.opsForHash();
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        hashOperations.put("TotalGameNum", "userid1", 1);
        hashOperations.put("TotalGameNum", "userid2", 2);
        hashOperations.put("TotalGameNum", "userid3", 3);
        zSetOperations.add("winRate", "userid1", 0.1);
        zSetOperations.add("winRate", "userid2", 0.2);
    }
}
