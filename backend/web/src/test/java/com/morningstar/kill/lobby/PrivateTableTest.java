package com.morningstar.kill.lobby;

import com.morningstar.kill.KillUser;
import com.morningstar.common.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PrivateTableTest {
    @Test
    public void test() {
        KillUser user1 = new KillUser(User.builder().username("小明").build());
        KillUser  user2 = new KillUser(User.builder().username("小白").build());
        KillUser  user3 = new KillUser(User.builder().username("小黄").build());
        Room room = new Room(user1);
        room.addMember(user2);
        room.addMember(user3);

        PrivateTable table = new PrivateTable(room);
        log.info("原始顺序: {}", table);
        table.shuffleSeat();
        log.info("打乱后的顺序: {}", table);
    }
}
