package com.morningstar.kill.exception;

public class SharedTableCreatedWithSameRoomException extends RuntimeException {
    public SharedTableCreatedWithSameRoomException() {
        super("使用了两个相同的房间创建SharedTable");
    }
}
