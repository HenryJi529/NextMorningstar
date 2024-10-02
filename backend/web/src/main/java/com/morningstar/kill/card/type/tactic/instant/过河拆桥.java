package com.morningstar.kill.card.type.tactic.instant;

import com.morningstar.kill.card.type.tactic.InstantTacticType;

/**
 * 使用时机：出牌阶段。
 * 使用目标：一名区域里有牌的其他角色。
 * 作用效果：你弃置目标对应的角色的区域里的一张牌。
 */
public class 过河拆桥 extends InstantTacticType {
    public 过河拆桥() {
        super(
                """
                        出牌阶段，对一名区域里有牌的其他角色使用。你弃置该角色区域内的一张牌。
                        """
        );
    }
}
