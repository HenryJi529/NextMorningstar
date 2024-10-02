package com.morningstar.kill.domain.exception;

public class UserAlreadyInRoomException extends RuntimeException{
    public UserAlreadyInRoomException(){
        super("用户已在房间中");
    }
}
