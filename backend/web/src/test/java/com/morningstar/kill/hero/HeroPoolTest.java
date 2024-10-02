package com.morningstar.kill.hero;

import com.morningstar.kill.hero.hero.刘备;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
public class HeroPoolTest {
    @Test
    public void test() {
        log.info(HeroPool.showHeroes());
        log.info(HeroPool.getHeroNames().toString());
        log.info(HeroPool.getLordNames().toString());
        Hero hero1 = HeroPool.getHeroByClass(刘备.class);
        Hero hero2 = HeroPool.getHeroByName("刘备");
        assert hero1 == hero2;
        // 测试随机获取N个英雄
        log.info(Arrays.toString(HeroPool.getRandomNHeroes(3)));
        log.info(Arrays.toString(HeroPool.getRandomNHeroes(3)));
        // 测试随机获取N个主公英雄
        log.info(Arrays.toString(HeroPool.getRandomNLords(2)));
        log.info(Arrays.toString(HeroPool.getRandomNLords(2)));
    }
}
