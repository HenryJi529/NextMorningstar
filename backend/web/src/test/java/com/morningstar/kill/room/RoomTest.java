package com.morningstar.kill.room;

import com.morningstar.common.pojo.po.User;
import org.junit.jupiter.api.Test;

public class RoomTest {
    @Test
    public void test() {
        Killer user1 = new Killer(User.builder().username("小白").build());
        Killer user2 = new Killer(User.builder().username("小红").build());
        Killer user3 = new Killer(User.builder().username("小黄").build());
        user1.createRoom();
        user1.inviteUser(user2);
        assert user1.getRoom().getMemberCount() == 2;
        user1.inviteUser(user3);
        assert user1.getRoom().getMemberCount() == 3;
        assert user1.getRoom() == user2.getRoom();
        assert user1.getRoom().getOwner() == user1;
        user1.exitRoom();
        assert user2.getRoom().getOwner() == user2;
        user2.closeRoom();
        assert user3.getRoom() == null;
    }
}
