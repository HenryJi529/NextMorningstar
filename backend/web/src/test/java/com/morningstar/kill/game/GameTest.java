package com.morningstar.kill.game;

import com.morningstar.kill.exception.GameModeNotSatisfiedByTableMemberCountException;
import com.morningstar.kill.exception.GameModeNotSupportedByTableTypeException;
import com.morningstar.kill.lobby.PrivateTable;
import com.morningstar.kill.lobby.Room;
import com.morningstar.kill.lobby.SharedTable;
import com.morningstar.kill.lobby.Table;
import com.morningstar.kill.KillUser;
import com.morningstar.common.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class GameTest {
    @Test
    public void test() {
        KillUser userA = new KillUser(User.builder().username("A").build());
        KillUser userB = new KillUser(User.builder().username("B").build());
        KillUser userC = new KillUser(User.builder().username("C").build());
        KillUser userD = new KillUser(User.builder().username("D").build());
        KillUser userE = new KillUser(User.builder().username("E").build());
        KillUser userF = new KillUser(User.builder().username("F").build());
        KillUser userG = new KillUser(User.builder().username("G").build());
        KillUser userH = new KillUser(User.builder().username("H").build());
        KillUser userI = new KillUser(User.builder().username("I").build());
        KillUser userJ = new KillUser(User.builder().username("J").build());
        Room room1 = new Room(userA, new LinkedList<>(List.of(userB)));
        Room room2 = new Room(userC, new LinkedList<>(List.of(userD)));
        Room room3 = new Room(userE, new LinkedList<>(List.of(userF, userG)));
        Room room4 = new Room(userH, new LinkedList<>(List.of(userI, userJ)));
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
