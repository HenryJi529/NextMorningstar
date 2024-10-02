package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

public class 万箭齐发 extends InstantTacticType {
    public 万箭齐发() {
        super(
                """
                        出牌阶段，对所有其他角色使用。每名目标角色需打出一张【闪】，否则受到你造成的1点伤害。
                        """
        );
    }
}
