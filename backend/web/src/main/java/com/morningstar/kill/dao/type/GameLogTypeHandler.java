package com.morningstar.kill.dao.type;

import com.morningstar.common.dao.type.JsonTypeHandler;
import com.morningstar.kill.game.GameLog;
import org.springframework.stereotype.Component;

@Component
public class GameLogTypeHandler extends JsonTypeHandler<GameLog> {
    public GameLogTypeHandler() {
        super(GameLog.class);
    }
}
