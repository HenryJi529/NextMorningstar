package com.morningstar.kill.lobby;

import com.morningstar.kill.KillUser;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;


/**
 * 某个房间(队伍)的牌桌
 */
@Getter
@Setter
public class PrivateTable extends Table {
    private final Room room;
    private final KillUser[] members;

    public PrivateTable(Room room) {
        this.room = room;
        // 拉好椅子
        members = new KillUser[room.getMemberCount()];
        // 成员上桌
        takeSeat();
    }

    /**
     * 房间中的人依次落座
     */
    private void takeSeat(){
        int i = 0;
        for(KillUser member : room.getMembers()) {
            members[i++] = member;
        }
    }

    /**
     * 打乱座位次序
     */
    public void shuffleSeat(){
        Random random = new Random();

        for (int i = members.length - 1; i > 0; i--) {
            int randomIndex = random.nextInt(i + 1);
            swapSeat(i, randomIndex);
        }
    }

    private void swapSeat(int i, int j){
        KillUser temp = members[i];
        members[i] = members[j];
        members[j] = temp;
    }
}
