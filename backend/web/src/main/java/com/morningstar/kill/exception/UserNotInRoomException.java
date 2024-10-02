package com.morningstar.kill.exception;

public class UserNotInRoomException extends RuntimeException {
    public UserNotInRoomException() {
        super("用户不在房间中");
    }
}
