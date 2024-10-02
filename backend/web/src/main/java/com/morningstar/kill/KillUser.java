package com.morningstar.kill;

import com.morningstar.kill.exception.UserAlreadyInRoomException;
import com.morningstar.kill.exception.UserNotInRoomException;
import com.morningstar.kill.lobby.Room;
import com.morningstar.common.pojo.po.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Deque;

@Data
@NoArgsConstructor
public class KillUser {
    private User user;

    // NOTE: 不修改直接通过自身修改room属性，避免逻辑混乱
    private Room room;

    public KillUser(User user) {
        this.user = user;
    }

    public void createRoom(){
        if(room != null){
            throw new UserAlreadyInRoomException();
        }
        new Room(this);
    }

    public void createRoomWithOthers(Deque<KillUser> users){
        if(room != null){
            throw new UserAlreadyInRoomException();
        }
        new Room(this, users);
    }

    public void exitRoom(){
        if(this.room == null){
            throw new UserNotInRoomException();
        }
        this.room.removeMember(this);
    }

    public void inviteUser(KillUser user){
        if(this.room == null){
            throw new UserNotInRoomException();
        }
        this.room.addMember(user);
    }

    public void closeRoom(){
        if(this.room == null){
            throw new UserNotInRoomException();
        }
        if(this.room.getOwner().equals(this)){
            this.room.close();
        }else{
            throw new RuntimeException("没有权限关闭房间");
        }
    }
}
