package com.morningstar.kill.domain.game.game;

import com.morningstar.kill.domain.game.Game;
import com.morningstar.kill.domain.game.Mode;
import com.morningstar.kill.domain.lobby.Table;

public class TriplesGame extends Game {
    private TriplesGame(Table table, Mode mode) {
        super(table, mode);
    }
}
