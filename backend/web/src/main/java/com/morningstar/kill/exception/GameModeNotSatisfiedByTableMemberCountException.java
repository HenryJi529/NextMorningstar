package com.morningstar.kill.exception;

public class GameModeNotSatisfiedByTableMemberCountException extends RuntimeException {
    public GameModeNotSatisfiedByTableMemberCountException() {
        super("牌桌人数不满足游戏模式要求");
    }
}
