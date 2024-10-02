package com.morningstar.kill.lobby;

import com.morningstar.kill.exception.SharedTableCreatedWithDifferentMemberCountsException;
import com.morningstar.kill.exception.SharedTableCreatedWithSameRoomException;
import com.morningstar.kill.exception.RoomNotExistException;
import com.morningstar.kill.KillUser;
import lombok.Getter;
import lombok.Setter;

/**
 * 两个房间(队伍)组成的牌桌【拼桌】
 */
@Getter
@Setter
public class SharedTable extends Table {
    private final Room room1;
    private final Room room2;
    private final KillUser[] members;

    public SharedTable(Room room1, Room room2) {
        if(room1 == null || room2 == null) {
            throw new RoomNotExistException();
        }
        if(room1 == room2){
            throw new SharedTableCreatedWithSameRoomException();
        }
        if(room1.getMemberCount() != room2.getMemberCount()) {
            throw new SharedTableCreatedWithDifferentMemberCountsException();
        }
        this.room1 = room1;
        this.room2 = room2;
        this.members = new KillUser[this.room1.getMemberCount() + this.room2.getMemberCount()];
        takeSeat();
    }

    /**
     * 两个房间的人隔着位置落座
     */
    private void takeSeat(){
        int i;
        i = 0;
        for(KillUser member : room1.getMembers()) {
            members[i] = member;
            i+=2;
        }
        i = 1;
        for(KillUser member : room2.getMembers()) {
            members[i] = member;
            i+=2;
        }
    }
}
