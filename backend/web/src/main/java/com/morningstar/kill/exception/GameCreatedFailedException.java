package com.morningstar.kill.exception;

public class GameCreatedFailedException extends RuntimeException {
    public GameCreatedFailedException(String message) {
        super(message);
    }
}
