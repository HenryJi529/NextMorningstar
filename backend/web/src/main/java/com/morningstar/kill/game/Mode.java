package com.morningstar.kill.game;

import com.morningstar.kill.game.game.*;
import lombok.Getter;

import java.util.Random;

/**
 * 游戏模式
 */
@Getter
public enum Mode {
    IDENTITY("身份场", 4, 10, IdentityGame.class),
    REVERT("斗地主", 3, 3, RevoltGame.class),
    NATION("国战", 2, 10, NationGame.class),
    SOLO("1V1", 2, 2, SoloGame.class),
    DOUBLES("2V2", 4, 4, DoublesGame.class),
    TRIPLES("3V3", 6, 6, TriplesGame.class);

    private final String name;
    private final Integer minHeadNum;
    private final Integer maxHeadNum;
    private final Class<? extends Game> GameClass;

    Mode(String name, Integer minHeadNum, Integer maxHeadNum, Class<? extends Game> GameClass) {
        this.name = name;
        this.minHeadNum = minHeadNum;
        this.maxHeadNum = maxHeadNum;
        this.GameClass = GameClass;
    }

    public static Mode getRandomMode() {
        Random random = new Random();
        Mode[] modes = Mode.values();
        return modes[random.nextInt(modes.length)];
    }

    @Override
    public String toString() {
        return name;
    }
}
