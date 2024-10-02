package com.morningstar.kill.exception;

public class GameDeckNotSelectedException extends RuntimeException {
    public GameDeckNotSelectedException() {
        super("游戏牌堆尚未被选择");
    }
}
