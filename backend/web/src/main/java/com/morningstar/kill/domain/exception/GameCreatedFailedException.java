package com.morningstar.kill.domain.exception;

public class GameCreatedFailedException extends RuntimeException{
    public GameCreatedFailedException(String message) {
        super(message);
    }
}
