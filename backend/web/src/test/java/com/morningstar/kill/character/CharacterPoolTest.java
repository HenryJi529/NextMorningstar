package com.morningstar.kill.character;

import com.morningstar.kill.character.characters.刘备;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
public class CharacterPoolTest {
    @Test
    public void test() {
        log.info(CharacterPool.showCharacters());
        log.info(CharacterPool.getCharacterNames().toString());
        log.info(CharacterPool.getLordNames().toString());
        Character character1 = CharacterPool.getCharacterByClass(刘备.class);
        Character character2 = CharacterPool.getCharacterByName("刘备");
        assert character1 == character2;
        // 测试随机获取N个英雄
        log.info(Arrays.toString(CharacterPool.getRandomNCharacters(3)));
        log.info(Arrays.toString(CharacterPool.getRandomNCharacters(3)));
        // 测试随机获取N个主公英雄
        log.info(Arrays.toString(CharacterPool.getRandomNLords(2)));
        log.info(Arrays.toString(CharacterPool.getRandomNLords(2)));
    }
}
