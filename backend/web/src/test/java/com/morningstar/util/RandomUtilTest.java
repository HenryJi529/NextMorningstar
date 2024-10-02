package com.morningstar.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
public class RandomUtilTest {
    @Test
    public void test1() {
        // 测试抽样
        log.info(Arrays.toString(RandomUtil.getSampleIndexes(3, 5)));
        log.info(Arrays.toString(RandomUtil.getSampleIndexes(3, 5)));
        // 测试打乱
        log.info(Arrays.toString(RandomUtil.getShuffledIndexes(5)));
        log.info(Arrays.toString(RandomUtil.getShuffledIndexes(5)));
    }

    @Test
    public void test2() {
        System.out.println(RandomUtil.getNumericString(6));
    }

    @Test
    public void test3(){
        System.out.println(RandomUtil.getString(10, "abc"));
    }
}
