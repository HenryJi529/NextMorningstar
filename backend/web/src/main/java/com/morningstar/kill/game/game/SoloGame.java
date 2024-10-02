package com.morningstar.kill.game.game;

import com.morningstar.kill.game.Game;
import com.morningstar.kill.game.Mode;
import com.morningstar.kill.room.Table;

/**
 * 1V1模式
 */
public class SoloGame extends Game {
    private SoloGame(Table table, Mode mode) {
        super(table, mode);
    }
}
