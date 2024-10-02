package com.morningstar.kill.domain.exception;

public class GameDeckNotSelectedException extends RuntimeException{
    public GameDeckNotSelectedException() {
        super("游戏牌堆尚未被选择");
    }
}
