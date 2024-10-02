package com.morningstar.kill.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
public class TestRandomUtil {
    @Test
    public void test() {
        // 测试抽样
        log.info(Arrays.toString(RandomUtil.getSampleIndexes(3,5)));
        log.info(Arrays.toString(RandomUtil.getSampleIndexes(3,5)));
        // 测试打乱
        log.info(Arrays.toString(RandomUtil.getShuffledIndexes(5)));
        log.info(Arrays.toString(RandomUtil.getShuffledIndexes(5)));
    }
}
