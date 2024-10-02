package com.morningstar.kill.domain.game.game;

import com.morningstar.kill.domain.game.Game;
import com.morningstar.kill.domain.game.Mode;
import com.morningstar.kill.domain.lobby.Table;

/**
 * 身份场
 */
public class IdentityGame extends Game {
    private IdentityGame(Table table, Mode mode){
        super(table, mode);
    }
}
