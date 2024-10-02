package com.morningstar.kill.domain.game.game;

import com.morningstar.kill.domain.game.Game;
import com.morningstar.kill.domain.game.Mode;
import com.morningstar.kill.domain.lobby.Table;

/**
 * 1V1模式
 */
public class SoloGame extends Game {
    private SoloGame(Table table, Mode mode) {
        super(table, mode);
    }
}
