package com.morningstar.kill.exception;

public class RoomNotExistException extends RuntimeException {
    public RoomNotExistException() {
        super("房间不存在");
    }
}
