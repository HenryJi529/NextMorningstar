package com.morningstar.kill.game;

import com.morningstar.kill.exception.GameIdAlreadyExistException;
import lombok.Data;

import java.util.HashMap;
import java.util.UUID;

@Data
public class GamePool {
    static HashMap<UUID, Game> store = new HashMap<>();

    public static void put(Game game) {
        if (store.containsKey(game.getId())) {
            throw new GameIdAlreadyExistException();
        }
        store.put(game.getId(), game);
    }

    public static Game get(UUID gameId) {
        return store.get(gameId);
    }

    public static void remove(UUID gameId) {
        store.remove(gameId);
    }
}
