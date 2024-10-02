package com.morningstar.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.morningstar.constant.RedisConstant;
import com.morningstar.util.JsonUtil;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    private GenericJackson2JsonRedisSerializer getRedisSerializer() {
        ObjectMapper mapper = JsonUtil.objectMapper();
        // 支持类型存储
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(), // 会检查类路径是否合法
                ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                JsonTypeInfo.As.PROPERTY
        );

        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    /**
     * 配置redisTemplate bean，并自定义数据的序列化的方式
     *
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
        template.setValueSerializer(getRedisSerializer());
        // 设置hash中field字段序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash中value的序列化方式
        template.setHashValueSerializer(getRedisSerializer());

        /* 初始化参数设置 */
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 构建本地缓存Bean对象
     *
     * @return Cache<String, Object>
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine
                .newBuilder()
                .maximumSize(200) // 设置缓存数量上限
//                .expireAfterAccess(1, TimeUnit.SECONDS) // 访问1秒后删除
//                .expireAfterWrite(1, TimeUnit.SECONDS) // 写入1秒后删除
                .initialCapacity(100) // 初始的缓存空间大小
                .recordStats() // 开启统计
                .build();
    }

    /**
     * 配置SpringCache
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 使用 Jackson2JsonRedisSerializer 作为序列化器
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(getRedisSerializer())
                )
                .entryTtl(Duration.ofHours(1))  // 设置默认 TTL
                .disableCachingNullValues()   // 不缓存 null 值
                .computePrefixWith(cacheName -> cacheName + ">");

        // 设置特有的Redis配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(RedisConstant.CACHE_BLOG_ARTICLE, config.entryTtl(Duration.ofSeconds(300)));
        cacheConfigurations.put(RedisConstant.CACHE_PIC_IMAGES, config.entryTtl(Duration.ofDays(7)));
        cacheConfigurations.put(RedisConstant.CACHE_PIC_DIRS, config.entryTtl(Duration.ofDays(7)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .transactionAware() // Cache事务支持
                .withInitialCacheConfigurations(cacheConfigurations)
                .cacheDefaults(config)
                .build();
    }
}
