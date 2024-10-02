package com.morningstar.kill.lobby;

import com.morningstar.common.pojo.po.User;
import com.morningstar.kill.KillUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SharedTableTest {
    @Test
    public void test() {
        KillUser user1 = new KillUser(User.builder().username("1.1").build());
        KillUser user2 = new KillUser(User.builder().username("1.2").build());
        KillUser user3 = new KillUser(User.builder().username("2.1").build());
        KillUser user4 = new KillUser(User.builder().username("2.2").build());
        user1.createRoom();
        user1.inviteUser(user2);
        user3.createRoom();
        user3.inviteUser(user4);
        SharedTable table = new SharedTable(user1.getRoom(), user3.getRoom());
        log.info(table.toString());
    }
}
