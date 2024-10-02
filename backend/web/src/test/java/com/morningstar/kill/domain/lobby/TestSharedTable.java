package com.morningstar.kill.domain.lobby;

import com.morningstar.kill.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestSharedTable {
    @Test
    public void test() {
        User user1 = User.builder().username("1.1").build();
        User user2 = User.builder().username("1.2").build();
        User user3 = User.builder().username("2.1").build();
        User user4 = User.builder().username("2.2").build();
        user1.createRoom();
        user1.inviteUser(user2);
        user3.createRoom();
        user3.inviteUser(user4);
        SharedTable table = new SharedTable(user1.getRoom(), user3.getRoom());
        log.info(table.toString());
    }
}
