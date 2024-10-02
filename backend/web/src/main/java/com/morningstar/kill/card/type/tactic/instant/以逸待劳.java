package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

public class 以逸待劳 extends InstantTacticType {
    public 以逸待劳() {
        super(
                """
                        出牌阶段，对你和与你势力相同的角色使用。每名目标角色各摸两张牌，然后弃置两张牌。
                        """
        );
    }
}
