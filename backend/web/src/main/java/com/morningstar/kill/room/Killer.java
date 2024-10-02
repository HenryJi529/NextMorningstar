package com.morningstar.kill.room;

import com.morningstar.common.pojo.po.User;
import com.morningstar.kill.exception.UserAlreadyInRoomException;
import com.morningstar.kill.exception.UserNotInRoomException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Deque;

@Data
@NoArgsConstructor
public class Killer {
    private User user;

    // NOTE: 不修改直接通过自身修改room属性，避免逻辑混乱
    private Room room;

    public Killer(User user) {
        this.user = user;
    }

    public void createRoom() {
        if (room != null) {
            throw new UserAlreadyInRoomException();
        }
        new Room(this);
    }

    public void createRoomWithOthers(Deque<Killer> users) {
        if (room != null) {
            throw new UserAlreadyInRoomException();
        }
        new Room(this, users);
    }

    public void exitRoom() {
        if (this.room == null) {
            throw new UserNotInRoomException();
        }
        this.room.removeMember(this);
    }

    public void inviteUser(Killer user) {
        if (this.room == null) {
            throw new UserNotInRoomException();
        }
        this.room.addMember(user);
    }

    public void closeRoom() {
        if (this.room == null) {
            throw new UserNotInRoomException();
        }
        if (this.room.getOwner().equals(this)) {
            this.room.close();
        } else {
            throw new RuntimeException("没有权限关闭房间");
        }
    }
}
