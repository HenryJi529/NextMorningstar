package com.morningstar.practice.lib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisTemplateTest {

    private final RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testStringOps() throws InterruptedException {
        redisTemplate.opsForValue().set("key1", "value1", 2, TimeUnit.SECONDS);
        assert Objects.equals(redisTemplate.opsForValue().get("key1"), "value1");
        redisTemplate.opsForValue().setIfAbsent("key1", "value2", 2, TimeUnit.SECONDS);
        assert Objects.equals(redisTemplate.opsForValue().get("key1"), "value1");
        redisTemplate.opsForValue().set("key2", "value2", 2, TimeUnit.SECONDS);
        Thread.sleep(2300);
        assert redisTemplate.opsForValue().get("key2") == null;
    }

    @Test
    public void testHashOps() {
        redisTemplate.opsForHash().put("hash1", "key1", "value1");
        assert Objects.equals(redisTemplate.opsForHash().get("hash1", "key1"), "value1");
        redisTemplate.opsForHash().put("hash1", "key2", "value2");
        redisTemplate.opsForHash().keys("hash1").forEach(System.out::println);
        redisTemplate.opsForHash().values("hash1").forEach(System.out::println);
        redisTemplate.opsForHash().entries("hash1").forEach((k, v) -> System.out.println(k + "=" + v));

        redisTemplate.expire("hash1", 2, TimeUnit.SECONDS);
    }

    @Test
    public void testListOps() {
        redisTemplate.opsForList().leftPush("list1", "value1");
        redisTemplate.opsForList().leftPush("list1", "value2");
        redisTemplate.opsForList().leftPushAll("list1", "value3", "value4", "value5");
        redisTemplate.opsForList().rightPop("list1");
        redisTemplate.opsForList().leftPop("list1");
        assert Objects.requireNonNull(redisTemplate.opsForList().size("list1")).intValue() == 3;
        assert List.of("value4", "value3", "value2").equals(redisTemplate.opsForList().range("list1", 0, -1));

        redisTemplate.expire("list1", 2, TimeUnit.SECONDS);
    }

    @Test
    public void testSetOps() {
        redisTemplate.opsForSet().add("set1", "value1", "value2", "value3");
        Set<Object> set = redisTemplate.opsForSet().members("set1");
        redisTemplate.opsForSet().remove("set1", "value3");

        redisTemplate.opsForSet().add("set2", "value2", "value3");

        assert (set != null ? set.size() : 0) == 3;
        assert Objects.requireNonNull(redisTemplate.opsForSet().size("set1")).intValue() == 2;
        assert Objects.equals(redisTemplate.opsForSet().union("set1", "set2"), Set.of("value1", "value2", "value3"));
        assert Objects.equals(redisTemplate.opsForSet().intersect("set1", "set2"), Set.of("value2"));
        assert Set.of("value1", "value2").contains((String) redisTemplate.opsForSet().randomMember("set1"));

        redisTemplate.expire("set1", 2, TimeUnit.SECONDS);
        redisTemplate.expire("set2", 2, TimeUnit.SECONDS);
    }

    @Test
    public void testZSetOps() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("zSet1", "a", 10);
        zSetOperations.add("zSet1", "b", 12);
        zSetOperations.add("zSet1", "c", 9);
        zSetOperations.add("zSet1", "c", 11);

        System.out.println(zSetOperations.range("zSet1", 0, -1));
        zSetOperations.incrementScore("zSet1", "c", 10);
        Objects.requireNonNull(zSetOperations.rangeWithScores("zSet1", 0, -1)).forEach(System.out::println);

        zSetOperations.remove("zSet1", "a", "b");
        assert Objects.equals(zSetOperations.size("zSet1"), 1L);

        redisTemplate.expire("zSet1", 2, TimeUnit.SECONDS);
    }

    @Test
    public void testCommonOps() throws InterruptedException {
        System.out.println(redisTemplate.keys("*"));

        redisTemplate.opsForValue().set("key1", "value1");
        assert redisTemplate.type("key1").equals(DataType.STRING);

        redisTemplate.expire("key1", 1, TimeUnit.SECONDS);
        assert redisTemplate.hasKey("key1");
        Thread.sleep(1300);
        assert !redisTemplate.hasKey("key1");

        redisTemplate.opsForValue().set("key2", "value2");
        assert redisTemplate.hasKey("key2");
        redisTemplate.delete("key2");
        assert !redisTemplate.hasKey("key2");
    }

    @Test
    public void testJson() {
        int i = new Random().nextInt();
        Person person1 = new Person("小王", Sex.FEMALE);
        redisTemplate.opsForValue().set("test" + i, person1);
        Person person2 = (Person) redisTemplate.opsForValue().get("test" + i);
        assert person1.equals(person2);

        redisTemplate.delete("test" + i);
    }

    enum Sex {
        MALE, FEMALE
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Person {
        private String name;
        private Sex sex;
    }
}
