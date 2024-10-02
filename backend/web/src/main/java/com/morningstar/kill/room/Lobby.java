package com.morningstar.kill.room;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 游戏大厅
 */
public class Lobby {
    static Map<UUID, Room> rooms = new HashMap<>();

    public static Room getRoom(UUID id) {
        return rooms.get(id);
    }

    public static void registerRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public static void deleteRoom(UUID id) {
        rooms.remove(id);
    }

    public static String showRooms() {
        return rooms.values().toString();
    }
}
