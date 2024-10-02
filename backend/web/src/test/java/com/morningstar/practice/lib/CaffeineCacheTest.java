package com.morningstar.practice.lib;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class CaffeineCacheTest {

    @Autowired
    private Cache<String, Object> caffeineCache;

    /**
     * 缓存的读写删
     */
    @Test
    public void test1() {
        List<Integer> data1 = Arrays.asList(100, 200, 300, 400);
        // 缓存数据
        caffeineCache.put("info1", data1);
        // 获取缓存数据
        Object data2 = caffeineCache.getIfPresent("info1");
        assert data1 == data2;
        // 刷新缓存
        List<Integer> data3 = Arrays.asList(500, 600, 700, 800);
        caffeineCache.put("info1", data3);
        Object data4 = caffeineCache.getIfPresent("info1");
        assert data3 == data4;
        assert data1 != data4;
        // 删除缓存
        caffeineCache.invalidate("info1");
        Object data5 = caffeineCache.getIfPresent("info1");
        assert data5 == null;
    }

    /**
     * 无缓存数据补救方式
     */
    @Test
    public void test2() {
        caffeineCache.cleanUp();
        List<Integer> data1 = Arrays.asList(100, 200, 300, 400);
        Object data2 = caffeineCache.get("info2", key -> {
            // 可以去数据库动态查询数据(redis, mysql)
            return data1;
        });
        Object data3 = caffeineCache.getIfPresent("info2");
        assert data1 == data2;
        assert data1 == data3;
    }

    /**
     * 缓存统计
     */
    @Test
    public void test3() {
        List<Integer> data = Arrays.asList(100, 200, 300, 400);
        // 缓存数据
        caffeineCache.put("info3", data);
        caffeineCache.put("obj", new Object());
        caffeineCache.put("user", "666");
        Object data1 = caffeineCache.getIfPresent("user");
        assert "666".equals(data1);
        CacheStats stats = caffeineCache.stats();// 返回统计数据
        System.out.println(stats);
        System.out.println(stats.hitRate()); // 命中率
        System.out.println(stats.evictionCount()); // 缓存回收数量
        System.out.println(stats.averageLoadPenalty()); // 加载新值花费的平均时间
    }
}
