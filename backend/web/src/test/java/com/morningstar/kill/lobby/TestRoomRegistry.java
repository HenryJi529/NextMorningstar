package com.morningstar.kill.lobby;

import com.morningstar.kill.KillUser;
import com.morningstar.common.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;

@Slf4j
public class TestRoomRegistry {
    @Test
    public void test() {
        KillUser user1 = new KillUser(User.builder().username("小白").build());
        KillUser user2 = new KillUser(User.builder().username("小红").build());
        user1.createRoomWithOthers(new LinkedList<>(Collections.singletonList(user2)));
        assert RoomRegistry.getRoom(user1.getRoom().getId()) == user2.getRoom();
        log.info(RoomRegistry.showRooms());
    }
}
