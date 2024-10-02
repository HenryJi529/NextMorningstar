package com.morningstar.kill.domain.game;

import com.morningstar.kill.domain.exception.GameModeNotSatisfiedByTableMemberCountException;
import com.morningstar.kill.domain.exception.GameModeNotSupportedByTableTypeException;
import com.morningstar.kill.domain.lobby.PrivateTable;
import com.morningstar.kill.domain.lobby.Room;
import com.morningstar.kill.domain.lobby.SharedTable;
import com.morningstar.kill.domain.lobby.Table;
import com.morningstar.kill.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class TestGame {
    @Test
    public void test() {
        Room room1 = new Room(User.builder().username("A").build(), new LinkedList<>(List.of(User.builder().username("B").build())));
        Room room2 = new Room(User.builder().username("C").build(), new LinkedList<>(List.of(User.builder().username("D").build())));
        Room room3 = new Room(User.builder().username("E").build(), new LinkedList<>(List.of(User.builder().username("F").build(), User.builder().username("G").build())));
        Room room4 = new Room(User.builder().username("H").build(), new LinkedList<>(List.of(User.builder().username("I").build(), User.builder().username("J").build())));
        Table table1 = new PrivateTable(room1);
        Table table2 = new SharedTable(room1, room2);
        Table table3 = new SharedTable(room3, room4);
        log.info("两人的房间可以玩1V1: {}", Game.createGame(table1, Mode.SOLO).toString());
        try{
            Game.createGame(table1, Mode.DOUBLES);
        }catch (GameModeNotSatisfiedByTableMemberCountException e){
            log.info("两人的房间玩不了2V2");
        }

        log.info("两个两人的房间可以玩2V2: {}", Game.createGame(table2, Mode.DOUBLES).toString());

        try{
            Game.createGame(table3, Mode.IDENTITY);
        }catch (GameModeNotSupportedByTableTypeException e){
            log.info("即便人数够，SharedRoom也不能玩国战");
        }
    }
}
