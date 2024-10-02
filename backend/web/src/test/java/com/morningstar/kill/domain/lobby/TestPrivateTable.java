package com.morningstar.kill.domain.lobby;

import com.morningstar.kill.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestPrivateTable {
    @Test
    public void test() {
        User user1 = User.builder().username("小明").build();
        User user2 = User.builder().username("小白").build();
        User user3 = User.builder().username("小黄").build();
        Room room = new Room(user1);
        room.addMember(user2);
        room.addMember(user3);

        PrivateTable table = new PrivateTable(room);
        log.info("原始顺序: {}", table);
        table.shuffleSeat();
        log.info("打乱后的顺序: {}", table);
    }
}
