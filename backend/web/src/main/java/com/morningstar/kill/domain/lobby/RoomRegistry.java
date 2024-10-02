package com.morningstar.kill.domain.lobby;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 房间注册表
 */
public class RoomRegistry {
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
    public static String showRooms(){
        return rooms.values().toString();
    }
}
