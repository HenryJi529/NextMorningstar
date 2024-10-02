package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

public class 五谷丰登 extends InstantTacticType {
    public 五谷丰登() {
        super(
                """
                        出牌阶段，对所有角色使用。你亮出牌堆顶等同于目标角色数的牌。每名目标角色获得其中的一张。
                        """
        );
    }
}
