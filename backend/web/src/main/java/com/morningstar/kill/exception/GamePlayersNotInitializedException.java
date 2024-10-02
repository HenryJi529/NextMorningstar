package com.morningstar.kill.exception;

public class GamePlayersNotInitializedException extends RuntimeException {
    public GamePlayersNotInitializedException() {
        super("游戏玩家尚未被初始化");
    }
}
