package com.morningstar.kill.domain.exception;

public class GameIdAlreadyExistException extends RuntimeException{
    public GameIdAlreadyExistException() {
        super("游戏Id已存在");
    }
}
