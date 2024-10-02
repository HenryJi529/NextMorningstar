package com.morningstar.kill.dao.type;

import com.morningstar.kill.domain.game.GameLog;
import org.springframework.stereotype.Component;

@Component
public class GameLogTypeHandler extends JsonTypeHandler<GameLog> {
    public GameLogTypeHandler() {
        super(GameLog.class);
    }
}
