package com.morningstar.kill.domain.game.game;

import com.morningstar.kill.domain.game.Game;
import com.morningstar.kill.domain.game.Mode;
import com.morningstar.kill.domain.lobby.Table;

/**
 * 2V2模式
 */
public class DoublesGame extends Game {
    private DoublesGame(Table table, Mode mode){
        super(table, mode);
    }
}
