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
        redisTemplate.opsForValue().set("key1", "value1");
        assert Objects.equals(redisTemplate.opsForValue().get("key1"), "value1");
        redisTemplate.opsForValue().setIfAbsent("key1", "value2");
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
    }

    @Test
    public void testListOps() {
        if (redisTemplate.hasKey("list1")) {
            redisTemplate.delete("list1");
        }
        redisTemplate.opsForList().leftPush("list1", "value1");
        redisTemplate.opsForList().leftPush("list1", "value2");
        redisTemplate.opsForList().leftPushAll("list1", "value3", "value4", "value5");
        redisTemplate.opsForList().rightPop("list1");
        assert Objects.requireNonNull(redisTemplate.opsForList().size("list1")).intValue() == 4;
        assert List.of("value5", "value4", "value3", "value2").equals(redisTemplate.opsForList().range("list1", 0, -1));
    }

    @Test
    public void testSetOps() {
        if (redisTemplate.hasKey("set1")) {
            redisTemplate.delete("set1");
        }
        if (redisTemplate.hasKey("set2")) {
            redisTemplate.delete("set2");
        }

        redisTemplate.opsForSet().add("set1", "value1", "value2", "value3");
        Set<Object> set = redisTemplate.opsForSet().members("set1");
        redisTemplate.opsForSet().remove("set1", "value3");
        redisTemplate.opsForSet().add("set2", "value2", "value3");

        assert Objects.requireNonNull(redisTemplate.opsForSet().size("set1")).intValue() == 2;
        assert Objects.equals(redisTemplate.opsForSet().union("set1", "set2"), Set.of("value1", "value2", "value3"));
        assert Objects.equals(redisTemplate.opsForSet().intersect("set1", "set2"), Set.of("value2"));
        assert Set.of("value1", "value2").contains((String) redisTemplate.opsForSet().randomMember("set1"));

        if (set != null) {
            set.forEach(System.out::println);
        }
    }

    @Test
    public void testZSetOps() {
        if (redisTemplate.hasKey("zSet1")) {
            redisTemplate.delete("zSet1");
        }
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

    }

    @Test
    public void testCommonOps() throws InterruptedException {
        System.out.println(redisTemplate.keys("*"));

        redisTemplate.opsForValue().set("key1", "value1");
        assert redisTemplate.type("key1").equals(DataType.STRING);
        redisTemplate.expire("key1", 1, TimeUnit.SECONDS);
        Thread.sleep(1300);
        assert !redisTemplate.hasKey("key1");

        redisTemplate.opsForValue().set("key2", "value2");
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
        System.out.println(person1);
        System.out.println(person2);
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
