package com.morningstar.kill.domain.exception;

public class GameModeNotSupportedByTableTypeException extends RuntimeException{
    public GameModeNotSupportedByTableTypeException(){
        super("牌桌类型不满足游戏模式要求");
    }
}
