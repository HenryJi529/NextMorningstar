package com.morningstar.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {
    /**
     * 配置redisTemplate bean，并自定义数据的序列化的方式
     * @param redisConnectionFactory 连接redis的工厂，底层有场景依赖启动时，自动加载
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        /* 构建RedisTemplate模板对象 */
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        /* 为不同的数据结构设置不同的序列化方案 */
        // 设置key序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        // 设置value序列化方式
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 设置hash中field字段序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash中value的序列化方式
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        /* 初始化参数设置 */
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 构建本地缓存Bean对象
     * @return Cache<String, Object>
     */
    @Bean
    public Cache<String, Object> caffeineCache(){
        return Caffeine
                .newBuilder()
                .maximumSize(200) // 设置缓存数量上限
//                .expireAfterAccess(1, TimeUnit.SECONDS) // 访问1秒后删除
//                .expireAfterWrite(1, TimeUnit.SECONDS) // 写入1秒后删除
                .initialCapacity(100) // 初始的缓存空间大小
                .recordStats() // 开启统计
                .build();
    }
}
