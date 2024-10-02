package com.morningstar.kill.room;

import com.morningstar.common.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;

@Slf4j
public class LobbyTest {
    @Test
    public void test() {
        Killer user1 = new Killer(User.builder().username("小白").build());
        Killer user2 = new Killer(User.builder().username("小红").build());
        user1.createRoomWithOthers(new LinkedList<>(Collections.singletonList(user2)));
        assert Lobby.getRoom(user1.getRoom().getId()) == user2.getRoom();
        log.info(Lobby.showRooms());
    }
}
