package com.morningstar.kill.room;

import com.morningstar.common.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TableTest {
    @Test
    public void test() {
        Killer user1 = new Killer(User.builder().username("小明").build());
        Killer user2 = new Killer(User.builder().username("小白").build());
        Killer user3 = new Killer(User.builder().username("小黄").build());
        Room room = new Room(user1);
        room.addMember(user2);
        room.addMember(user3);

        Table table = new Table(room);
        log.info("原始顺序: {}", table);
        table.shuffleSeat();
        log.info("打乱后的顺序: {}", table);
    }
}
